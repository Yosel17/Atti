package yosel.dev.atti.screens.pre_anesthetic_test_form.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.models.model.PreAnestheticTestModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.pre_anesthetic_test_form.domain.PreAnestheticTestFormRepository
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = PreAnestheticTestFormViewModel.Factory::class)
class PreAnestheticTestFormViewModel @AssistedInject constructor(
    private val repository: PreAnestheticTestFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("preAnestheticTestId") private val preAnestheticTestId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("preAnestheticTestId") preAnestheticTestId: String?
        ): PreAnestheticTestFormViewModel
    }

    private val _state = MutableStateFlow(
        PreAnestheticTestFormState(
            isEditMode = !preAnestheticTestId.isNullOrBlank(),
            preAnestheticTestId = preAnestheticTestId
        )
    )
    val state: StateFlow<PreAnestheticTestFormState> = _state

    private val _eventChannel = Channel<PreAnestheticTestFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        observeProductsAndServices()
        syncProductsAndServices()
        loadInitialData()
    }

    fun onAction(action: PreAnestheticTestFormAction) {
        when (action) {
            PreAnestheticTestFormAction.TryLoadAgain -> {
                syncProductsAndServices()
                loadInitialData()
            }
            is PreAnestheticTestFormAction.OnTabSelected -> {
                _state.update { it.copy(currentTab = action.tab) }
            }
            PreAnestheticTestFormAction.SavePreAnestheticTest -> savePreAnestheticTests()
            is PreAnestheticTestFormAction.ToggleSaveDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }
            is PreAnestheticTestFormAction.OnNotesChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(notes = action.notes)) }
            }

            // BottomSheet Productos
            PreAnestheticTestFormAction.OnOpenProductSheet -> handleOpenProductSheet()
            PreAnestheticTestFormAction.OnDismissProductSheet -> {
                _state.update { it.copy(isProductSheetOpen = false) }
            }
            is PreAnestheticTestFormAction.OnProductSearchQueryChange -> {
                _state.update { it.copy(productSearchQuery = action.query) }
                debounceSearch { filterProducts(action.query) }
            }
            is PreAnestheticTestFormAction.OnToggleSelectProduct -> toggleSelectProduct(action.product)
            PreAnestheticTestFormAction.OnConfirmProductSelection -> confirmProductSelection()

            // BottomSheet Servicios
            PreAnestheticTestFormAction.OnOpenServiceSheet -> handleOpenServiceSheet()
            PreAnestheticTestFormAction.OnDismissServiceSheet -> {
                _state.update { it.copy(isServiceSheetOpen = false) }
            }
            is PreAnestheticTestFormAction.OnServiceSearchQueryChange -> {
                _state.update { it.copy(serviceSearchQuery = action.query) }
                debounceSearch { filterServices(action.query) }
            }
            is PreAnestheticTestFormAction.OnToggleSelectService -> toggleSelectService(action.service)
            PreAnestheticTestFormAction.OnConfirmServiceSelection -> confirmServiceSelection()

            // Modificación de ítems seleccionados
            is PreAnestheticTestFormAction.OnIncrementProduct -> incrementProduct(action.productId)
            is PreAnestheticTestFormAction.OnDecrementProduct -> decrementProduct(action.productId)
            is PreAnestheticTestFormAction.OnRemoveProduct -> removeProduct(action.productId)

            is PreAnestheticTestFormAction.OnIncrementService -> incrementService(action.serviceId)
            is PreAnestheticTestFormAction.OnDecrementService -> decrementService(action.serviceId)
            is PreAnestheticTestFormAction.OnRemoveService -> removeService(action.serviceId)
        }
    }

    private fun observeProductsAndServices() {
        viewModelScope.launch {
            combine(
                repository.getActiveProductsWithDetailsFlow(),
                repository.getActiveServicesWithDetailsFlow()
            ) { products, services ->
                products to services
            }
                .catch { error ->
                    Log.e("PreAnestheticTestVM", "Error al observar productos y servicios", error)
                    _eventChannel.send(PreAnestheticTestFormEvent.ShowErrorSnackbar("Error al observar productos y servicios."))
                }
                .collectLatest { (products, services) ->
                    _state.update { currentState ->
                        val updatedState = currentState.copy(
                            productsWithDetails = products,
                            servicesWithDetails = services,
                            isSuccessGetData = true
                        )

                        val newFilteredProducts = if (currentState.isProductSheetOpen) {
                            getFilteredAndSortedProducts(
                                products = products,
                                query = currentState.productSearchQuery,
                                selectedIds = currentState.tempSelectedProductIds
                            )
                        } else updatedState.filteredProducts

                        val newFilteredServices = if (currentState.isServiceSheetOpen) {
                            getFilteredAndSortedServices(
                                services = services,
                                query = currentState.serviceSearchQuery,
                                selectedIds = currentState.tempSelectedServiceIds
                            )
                        } else updatedState.filteredServices

                        val updatedSelectedProducts = currentState.formInputState.selectedProducts.map { selected ->
                            val updatedProd = products.find { it.product.id == selected.productWithDetails.product.id }
                            if (updatedProd != null) selected.copy(productWithDetails = updatedProd) else selected
                        }

                        val updatedSelectedServices = currentState.formInputState.selectedServices.map { selected ->
                            val updatedServ = services.find { it.service.id == selected.serviceWithDetails.service.id }
                            if (updatedServ != null) selected.copy(serviceWithDetails = updatedServ) else selected
                        }

                        updatedState.copy(
                            filteredProducts = newFilteredProducts,
                            filteredServices = newFilteredServices,
                            formInputState = updatedState.formInputState.copy(
                                selectedProducts = updatedSelectedProducts,
                                selectedServices = updatedSelectedServices
                            )
                        )
                    }
                }
        }
    }

    private fun syncProductsAndServices() {
        viewModelScope.launch {
            val productsSync = repository.syncProducts()
            val servicesSync = repository.syncServices()

            if (productsSync.isFailure || servicesSync.isFailure) {
                _eventChannel.send(
                    PreAnestheticTestFormEvent.ShowErrorSnackbar("No pudimos sincronizar algunos productos o servicios.")
                )
            }
        }
    }

    private fun loadInitialData() {
        _state.update { it.copy(isLoadingDataInitial = true) }
        viewModelScope.launch {
            repository.getConsultation(consultationId.orEmpty()).fold(
                onSuccess = { consultation ->
                    _state.update { it.copy(consultationWithDetails = consultation) }
                    loadFormContent()
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(PreAnestheticTestFormEvent.ShowErrorSnackbar("No se pudo cargar la información de la consulta."))
                }
            )
        }
    }

    private suspend fun loadFormContent() {
        val products = repository.getActiveProductsWithDetailsFlow().first()
        val services = repository.getActiveServicesWithDetailsFlow().first()

        if (_state.value.isEditMode) {
            loadExistingPreAnestheticTests(products, services)
        } else {
            _state.update { it.copy(isLoadingDataInitial = false) }
        }
    }

    private fun loadExistingPreAnestheticTests(
        products: List<ProductWithDetailsModel>,
        services: List<ServiceWithDetailsModel>
    ) {
        viewModelScope.launch {
            repository.getPreAnestheticTestsByConsultationId(consultationId.orEmpty()).fold(
                onSuccess = { existingList ->
                    val selectedProducts = existingList.mapNotNull { testWithDetails ->
                        val prod = testWithDetails.product
                            ?: products.find { it.product.id == testWithDetails.preAnestheticTest.productId }
                        prod?.let {
                            SelectedPreAnestheticProduct(
                                productWithDetails = it,
                                quantity = testWithDetails.preAnestheticTest.quantity.toInt().coerceAtLeast(1)
                            )
                        }
                    }

                    val selectedServices = existingList.mapNotNull { testWithDetails ->
                        val serv = testWithDetails.service
                            ?: services.find { it.service.id == testWithDetails.preAnestheticTest.serviceId }
                        serv?.let {
                            SelectedPreAnestheticService(
                                serviceWithDetails = it,
                                quantity = testWithDetails.preAnestheticTest.quantity.toInt().coerceAtLeast(1)
                            )
                        }
                    }

                    val existingNotes = existingList.firstOrNull()?.preAnestheticTest?.notes.orEmpty()

                    val inputs = PreAnestheticTestFormInputsState(
                        selectedProducts = selectedProducts,
                        selectedServices = selectedServices,
                        notes = existingNotes
                    )

                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingPreAnestheticTestsWithDetails = existingList,
                            formInputState = inputs,
                            initialFormInputState = inputs,
                            isLoadingDataInitial = false
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(PreAnestheticTestFormEvent.ShowErrorSnackbar("No se pudieron cargar los exámenes pre anestésicos previos."))
                }
            )
        }
    }

    // --- Productos BottomSheet & Filtro ---
    private fun handleOpenProductSheet() {
        val currentSelectedIds = _state.value.formInputState.selectedProducts.map { it.productWithDetails.product.id }.toSet()
        val sorted = getFilteredAndSortedProducts(
            products = _state.value.productsWithDetails,
            query = "",
            selectedIds = currentSelectedIds
        )
        _state.update {
            it.copy(
                productSearchQuery = "",
                tempSelectedProductIds = currentSelectedIds,
                filteredProducts = sorted,
                isProductSheetOpen = true
            )
        }
    }

    private fun filterProducts(query: String) {
        _state.update { s ->
            val sorted = getFilteredAndSortedProducts(
                products = s.productsWithDetails,
                query = query,
                selectedIds = s.tempSelectedProductIds
            )
            s.copy(filteredProducts = sorted)
        }
    }

    private fun toggleSelectProduct(product: ProductWithDetailsModel) {
        if (product.product.stock <= 0) return
        _state.update { s ->
            val newSelection = if (s.tempSelectedProductIds.contains(product.product.id)) {
                s.tempSelectedProductIds - product.product.id
            } else {
                s.tempSelectedProductIds + product.product.id
            }
            val sorted = getFilteredAndSortedProducts(
                products = s.productsWithDetails,
                query = s.productSearchQuery,
                selectedIds = newSelection
            )
            s.copy(tempSelectedProductIds = newSelection, filteredProducts = sorted)
        }
    }

    private fun getFilteredAndSortedProducts(
        products: List<ProductWithDetailsModel>,
        query: String,
        selectedIds: Set<String>
    ): List<ProductWithDetailsModel> {
        val normalizedQuery = query.normalize()
        val filtered = products.filter { item ->
            val matchesQuery = if (normalizedQuery.isBlank()) true else {
                item.product.commercialName.normalize().contains(normalizedQuery) ||
                        item.product.brand.normalize().contains(normalizedQuery)
            }
            matchesQuery
        }

        return filtered.sortedWith(
            compareByDescending<ProductWithDetailsModel> { selectedIds.contains(it.product.id) }
                .thenBy { it.product.commercialName.lowercase() }
        )
    }

    private fun confirmProductSelection() {
        val s = _state.value
        val existingMap = s.formInputState.selectedProducts.associateBy { it.productWithDetails.product.id }
        val newSelection = s.productsWithDetails
            .filter { s.tempSelectedProductIds.contains(it.product.id) }
            .map { item ->
                existingMap[item.product.id] ?: SelectedPreAnestheticProduct(productWithDetails = item, quantity = 1)
            }

        _state.update {
            it.copy(
                isProductSheetOpen = false,
                formInputState = it.formInputState.copy(selectedProducts = newSelection)
            )
        }
    }

    // --- Servicios BottomSheet & Filtro ---
    private fun handleOpenServiceSheet() {
        val currentSelectedIds = _state.value.formInputState.selectedServices.map { it.serviceWithDetails.service.id }.toSet()
        val sorted = getFilteredAndSortedServices(
            services = _state.value.servicesWithDetails,
            query = "",
            selectedIds = currentSelectedIds
        )
        _state.update {
            it.copy(
                serviceSearchQuery = "",
                tempSelectedServiceIds = currentSelectedIds,
                filteredServices = sorted,
                isServiceSheetOpen = true
            )
        }
    }

    private fun filterServices(query: String) {
        _state.update { s ->
            val sorted = getFilteredAndSortedServices(
                services = s.servicesWithDetails,
                query = query,
                selectedIds = s.tempSelectedServiceIds
            )
            s.copy(filteredServices = sorted)
        }
    }

    private fun toggleSelectService(service: ServiceWithDetailsModel) {
        if (!isServiceSelectable(service)) return
        _state.update { s ->
            val newSelection = if (s.tempSelectedServiceIds.contains(service.service.id)) {
                s.tempSelectedServiceIds - service.service.id
            } else {
                s.tempSelectedServiceIds + service.service.id
            }
            val sorted = getFilteredAndSortedServices(
                services = s.servicesWithDetails,
                query = s.serviceSearchQuery,
                selectedIds = newSelection
            )
            s.copy(tempSelectedServiceIds = newSelection, filteredServices = sorted)
        }
    }

    private fun getFilteredAndSortedServices(
        services: List<ServiceWithDetailsModel>,
        query: String,
        selectedIds: Set<String>
    ): List<ServiceWithDetailsModel> {
        val normalizedQuery = query.normalize()
        val filtered = services.filter { item ->
            if (normalizedQuery.isBlank()) true else {
                item.service.name.normalize().contains(normalizedQuery)
            }
        }

        return filtered.sortedWith(
            compareByDescending<ServiceWithDetailsModel> { selectedIds.contains(it.service.id) }
                .thenBy { it.service.name.lowercase() }
        )
    }

    private fun confirmServiceSelection() {
        val s = _state.value
        val existingMap = s.formInputState.selectedServices.associateBy { it.serviceWithDetails.service.id }
        val newSelection = s.servicesWithDetails
            .filter { s.tempSelectedServiceIds.contains(it.service.id) }
            .map { item ->
                existingMap[item.service.id] ?: SelectedPreAnestheticService(serviceWithDetails = item, quantity = 1)
            }

        _state.update {
            it.copy(
                isServiceSheetOpen = false,
                formInputState = it.formInputState.copy(selectedServices = newSelection)
            )
        }
    }

    // --- Lógica de Incremento / Decremento / Remoción ---
    private fun incrementProduct(productId: String) {
        _state.update { s ->
            val updated = s.formInputState.selectedProducts.map { item ->
                if (item.productWithDetails.product.id == productId) {
                    if (item.quantity < item.productWithDetails.product.stock) {
                        item.copy(quantity = item.quantity + 1)
                    } else item
                } else item
            }
            s.copy(formInputState = s.formInputState.copy(selectedProducts = updated))
        }
    }

    private fun decrementProduct(productId: String) {
        _state.update { s ->
            val updated = s.formInputState.selectedProducts.map { item ->
                if (item.productWithDetails.product.id == productId && item.quantity > 1) {
                    item.copy(quantity = item.quantity - 1)
                } else item
            }
            s.copy(formInputState = s.formInputState.copy(selectedProducts = updated))
        }
    }

    private fun removeProduct(productId: String) {
        _state.update { s ->
            val updated = s.formInputState.selectedProducts.filterNot { it.productWithDetails.product.id == productId }
            s.copy(formInputState = s.formInputState.copy(selectedProducts = updated))
        }
    }

    private fun incrementService(serviceId: String) {
        _state.update { s ->
            val updated = s.formInputState.selectedServices.map { item ->
                if (item.serviceWithDetails.service.id == serviceId) {
                    if (canIncrementService(item.serviceWithDetails, item.quantity)) {
                        item.copy(quantity = item.quantity + 1)
                    } else {
                        viewModelScope.launch {
                            _eventChannel.send(PreAnestheticTestFormEvent.ShowToast("Stock de suministros insuficiente para aumentar la cantidad."))
                        }
                        item
                    }
                } else item
            }
            s.copy(formInputState = s.formInputState.copy(selectedServices = updated))
        }
    }

    private fun decrementService(serviceId: String) {
        _state.update { s ->
            val updated = s.formInputState.selectedServices.map { item ->
                if (item.serviceWithDetails.service.id == serviceId && item.quantity > 1) {
                    item.copy(quantity = item.quantity - 1)
                } else item
            }
            s.copy(formInputState = s.formInputState.copy(selectedServices = updated))
        }
    }

    private fun removeService(serviceId: String) {
        _state.update { s ->
            val updated = s.formInputState.selectedServices.filterNot { it.serviceWithDetails.service.id == serviceId }
            s.copy(formInputState = s.formInputState.copy(selectedServices = updated))
        }
    }

    // --- Validadores de Suministros ---
    private fun isServiceSelectable(service: ServiceWithDetailsModel): Boolean {
        if (service.supplies.isEmpty()) return true
        return service.supplies.none { it.product.product.stock <= 0 }
    }

    private fun canIncrementService(service: ServiceWithDetailsModel, currentQuantity: Int): Boolean {
        if (service.supplies.isEmpty()) return true
        val targetQuantity = currentQuantity + 1
        return service.supplies.all { supplyItem ->
            val required = supplyItem.supply.quantityRequired * targetQuantity
            supplyItem.product.product.stock >= required
        }
    }

    // --- Persistencia ---
    private fun savePreAnestheticTests() {
        val s = _state.value
        if (!s.formInputState.isValid) return
        if (s.isEditMode) {
            updatePreAnestheticTests()
        } else {
            registerPreAnestheticTests()
        }
    }

    private fun buildPreAnestheticTestModels(): List<PreAnestheticTestModel> {
        val s = _state.value
        val productTests = s.formInputState.selectedProducts.map {
            PreAnestheticTestModel(
                consultationId = consultationId.orEmpty(),
                productId = it.productWithDetails.product.id,
                serviceId = null,
                quantity = it.quantity.toDouble(),
                notes = s.formInputState.notes,
                status = Constants.ACTIVE_STATUS
            )
        }
        val serviceTests = s.formInputState.selectedServices.map {
            PreAnestheticTestModel(
                consultationId = consultationId.orEmpty(),
                productId = null,
                serviceId = it.serviceWithDetails.service.id,
                quantity = it.quantity.toDouble(),
                notes = s.formInputState.notes,
                status = Constants.ACTIVE_STATUS
            )
        }
        return productTests + serviceTests
    }

    private fun registerPreAnestheticTests() {
        val s = _state.value
        _state.update { it.copy(isLoadingSavePreAnestheticTest = true) }
        viewModelScope.launch {
            repository.savePreAnestheticTests(
                consultationId = consultationId.orEmpty(),
                tests = buildPreAnestheticTestModels()
            ).fold(
                onSuccess = { savedList ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingPreAnestheticTestsWithDetails = savedList,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSavePreAnestheticTest = false
                        )
                    }
                    _eventChannel.send(PreAnestheticTestFormEvent.ShowSuccessSnackbar("Exámenes pre anestésicos registrados correctamente."))
                },
                onFailure = { error ->
                    Log.e("PreAnestheticTestVM", "Error al guardar exámenes pre anestésicos", error)
                    _state.update { it.copy(isLoadingSavePreAnestheticTest = false) }
                    _eventChannel.send(PreAnestheticTestFormEvent.ShowErrorSnackbar("Error al guardar los exámenes pre anestésicos."))
                }
            )
        }
    }

    private fun updatePreAnestheticTests() {
        val s = _state.value
        _state.update { it.copy(isLoadingUpdatePreAnestheticTest = true) }
        viewModelScope.launch {
            repository.updatePreAnestheticTests(
                consultationId = consultationId.orEmpty(),
                tests = buildPreAnestheticTestModels()
            ).fold(
                onSuccess = { updatedList ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingPreAnestheticTestsWithDetails = updatedList,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingUpdatePreAnestheticTest = false
                        )
                    }
                    _eventChannel.send(PreAnestheticTestFormEvent.ShowSuccessSnackbar("Exámenes pre anestésicos actualizados correctamente."))
                },
                onFailure = { error ->
                    Log.e("PreAnestheticTestVM", "Error al actualizar exámenes pre anestésicos", error)
                    _state.update { it.copy(isLoadingUpdatePreAnestheticTest = false) }
                    _eventChannel.send(PreAnestheticTestFormEvent.ShowErrorSnackbar("Error al actualizar los exámenes pre anestésicos."))
                }
            )
        }
    }

    private fun debounceSearch(block: () -> Unit) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300.milliseconds)
            block()
        }
    }
}
