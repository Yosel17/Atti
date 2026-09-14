package yosel.dev.atti.screens.shift_medication_form.ui

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
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.ShiftMedicationModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.shift_medication_form.domain.ShiftMedicationFormRepository
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = ShiftMedicationFormViewModel.Factory::class)
class ShiftMedicationFormViewModel @AssistedInject constructor(
    private val repository: ShiftMedicationFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("shiftMedicationId") private val shiftMedicationId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("shiftMedicationId") shiftMedicationId: String?
        ): ShiftMedicationFormViewModel
    }

    private val _state = MutableStateFlow(
        ShiftMedicationFormState(
            isEditMode = !shiftMedicationId.isNullOrBlank(),
            shiftMedicationId = shiftMedicationId
        )
    )
    val state: StateFlow<ShiftMedicationFormState> = _state

    private val _eventChannel = Channel<ShiftMedicationFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        observeProductsAndServices()
        syncProductsAndServices()
        loadInitialData()
    }

    fun onAction(action: ShiftMedicationFormAction) {
        when (action) {
            ShiftMedicationFormAction.TryLoadAgain -> {
                syncProductsAndServices()
                loadInitialData()
            }
            is ShiftMedicationFormAction.OnTabSelected -> {
                _state.update { it.copy(currentTab = action.tab) }
            }
            ShiftMedicationFormAction.SaveShiftMedication -> saveShiftMedications()
            is ShiftMedicationFormAction.ToggleSaveDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }
            is ShiftMedicationFormAction.OnNotesChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(notes = action.notes)) }
            }

            // BottomSheet Productos
            ShiftMedicationFormAction.OnOpenProductSheet -> handleOpenProductSheet()
            ShiftMedicationFormAction.OnDismissProductSheet -> {
                _state.update { it.copy(isProductSheetOpen = false) }
            }
            is ShiftMedicationFormAction.OnProductSearchQueryChange -> {
                _state.update { it.copy(productSearchQuery = action.query) }
                debounceSearch { filterProducts(action.query) }
            }
            is ShiftMedicationFormAction.OnToggleSelectProduct -> toggleSelectProduct(action.product)
            ShiftMedicationFormAction.OnConfirmProductSelection -> confirmProductSelection()

            // BottomSheet Servicios
            ShiftMedicationFormAction.OnOpenServiceSheet -> handleOpenServiceSheet()
            ShiftMedicationFormAction.OnDismissServiceSheet -> {
                _state.update { it.copy(isServiceSheetOpen = false) }
            }
            is ShiftMedicationFormAction.OnServiceSearchQueryChange -> {
                _state.update { it.copy(serviceSearchQuery = action.query) }
                debounceSearch { filterServices(action.query) }
            }
            is ShiftMedicationFormAction.OnToggleSelectService -> toggleSelectService(action.service)
            ShiftMedicationFormAction.OnConfirmServiceSelection -> confirmServiceSelection()

            // Modificación de ítems seleccionados
            is ShiftMedicationFormAction.OnIncrementProduct -> incrementProduct(action.productId)
            is ShiftMedicationFormAction.OnDecrementProduct -> decrementProduct(action.productId)
            is ShiftMedicationFormAction.OnRemoveProduct -> removeProduct(action.productId)

            is ShiftMedicationFormAction.OnIncrementService -> incrementService(action.serviceId)
            is ShiftMedicationFormAction.OnDecrementService -> decrementService(action.serviceId)
            is ShiftMedicationFormAction.OnRemoveService -> removeService(action.serviceId)
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
                    Log.e("ShiftMedicationVM", "Error al observar productos y servicios", error)
                    _eventChannel.send(ShiftMedicationFormEvent.ShowErrorSnackbar("Error al observar productos y servicios."))
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
                    ShiftMedicationFormEvent.ShowErrorSnackbar("No pudimos sincronizar algunos productos o servicios.")
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
                    _eventChannel.send(ShiftMedicationFormEvent.ShowErrorSnackbar("No se pudo cargar la información de la consulta."))
                }
            )
        }
    }

    private suspend fun loadFormContent() {
        val products = repository.getActiveProductsWithDetailsFlow().first()
        val services = repository.getActiveServicesWithDetailsFlow().first()

        if (_state.value.isEditMode) {
            loadExistingShiftMedications(products, services)
        } else {
            _state.update { it.copy(isLoadingDataInitial = false) }
        }
    }

    private fun loadExistingShiftMedications(
        products: List<ProductWithDetailsModel>,
        services: List<ServiceWithDetailsModel>
    ) {
        viewModelScope.launch {
            repository.getShiftMedicationsByConsultationId(consultationId.orEmpty()).fold(
                onSuccess = { existingList ->
                    val selectedProducts = existingList.mapNotNull { medicationWithDetails ->
                        val prod = medicationWithDetails.product
                            ?: products.find { it.product.id == medicationWithDetails.shiftMedication.productId }
                        prod?.let {
                            SelectedShiftMedicationProduct(
                                productWithDetails = it,
                                quantity = medicationWithDetails.shiftMedication.quantity.toInt().coerceAtLeast(1)
                            )
                        }
                    }

                    val selectedServices = existingList.mapNotNull { medicationWithDetails ->
                        val serv = medicationWithDetails.service
                            ?: services.find { it.service.id == medicationWithDetails.shiftMedication.serviceId }
                        serv?.let {
                            SelectedShiftMedicationService(
                                serviceWithDetails = it,
                                quantity = medicationWithDetails.shiftMedication.quantity.toInt().coerceAtLeast(1)
                            )
                        }
                    }

                    val existingNotes = existingList.firstOrNull()?.shiftMedication?.notes.orEmpty()

                    val inputs = ShiftMedicationFormInputsState(
                        selectedProducts = selectedProducts,
                        selectedServices = selectedServices,
                        notes = existingNotes
                    )

                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingShiftMedicationsWithDetails = existingList,
                            formInputState = inputs,
                            initialFormInputState = inputs,
                            isLoadingDataInitial = false
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(ShiftMedicationFormEvent.ShowErrorSnackbar("No se pudieron cargar los fármacos de turno previos."))
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
                existingMap[item.product.id] ?: SelectedShiftMedicationProduct(productWithDetails = item, quantity = 1)
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
                existingMap[item.service.id] ?: SelectedShiftMedicationService(serviceWithDetails = item, quantity = 1)
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
                            _eventChannel.send(ShiftMedicationFormEvent.ShowToast("Stock de suministros insuficiente para aumentar la cantidad."))
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
    private fun saveShiftMedications() {
        val s = _state.value
        if (!s.formInputState.isValid) return
        if (s.isEditMode) {
            updateShiftMedications()
        } else {
            registerShiftMedications()
        }
    }

    private fun buildShiftMedicationModels(): List<ShiftMedicationModel> {
        val s = _state.value
        val productMedications = s.formInputState.selectedProducts.map {
            ShiftMedicationModel(
                consultationId = consultationId.orEmpty(),
                productId = it.productWithDetails.product.id,
                serviceId = null,
                quantity = it.quantity.toDouble(),
                notes = s.formInputState.notes,
                status = Constants.ACTIVE_STATUS
            )
        }
        val serviceMedications = s.formInputState.selectedServices.map {
            ShiftMedicationModel(
                consultationId = consultationId.orEmpty(),
                productId = null,
                serviceId = it.serviceWithDetails.service.id,
                quantity = it.quantity.toDouble(),
                notes = s.formInputState.notes,
                status = Constants.ACTIVE_STATUS
            )
        }
        return productMedications + serviceMedications
    }

    private fun registerShiftMedications() {
        val s = _state.value
        _state.update { it.copy(isLoadingSaveShiftMedication = true) }
        viewModelScope.launch {
            repository.saveShiftMedications(
                consultationId = consultationId.orEmpty(),
                medications = buildShiftMedicationModels()
            ).fold(
                onSuccess = { savedList ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingShiftMedicationsWithDetails = savedList,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSaveShiftMedication = false
                        )
                    }
                    _eventChannel.send(ShiftMedicationFormEvent.ShowSuccessSnackbar("Fármacos de turno registrados correctamente."))
                },
                onFailure = { error ->
                    Log.e("ShiftMedicationVM", "Error al guardar fármacos de turno", error)
                    _state.update { it.copy(isLoadingSaveShiftMedication = false) }
                    _eventChannel.send(ShiftMedicationFormEvent.ShowErrorSnackbar("Error al guardar los fármacos de turno."))
                }
            )
        }
    }

    private fun updateShiftMedications() {
        val s = _state.value
        _state.update { it.copy(isLoadingUpdateShiftMedication = true) }
        viewModelScope.launch {
            repository.updateShiftMedications(
                consultationId = consultationId.orEmpty(),
                medications = buildShiftMedicationModels()
            ).fold(
                onSuccess = { updatedList ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingShiftMedicationsWithDetails = updatedList,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingUpdateShiftMedication = false
                        )
                    }
                    _eventChannel.send(ShiftMedicationFormEvent.ShowSuccessSnackbar("Fármacos de turno actualizados correctamente."))
                },
                onFailure = { error ->
                    Log.e("ShiftMedicationVM", "Error al actualizar fármacos de turno", error)
                    _state.update { it.copy(isLoadingUpdateShiftMedication = false) }
                    _eventChannel.send(ShiftMedicationFormEvent.ShowErrorSnackbar("Error al actualizar los fármacos de turno."))
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
