package yosel.dev.atti.screens.treatment_form.ui

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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.TreatmentModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.treatment_form.domain.TreatmentFormRepository
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = TreatmentFormViewModel.Factory::class)
class TreatmentFormViewModel @AssistedInject constructor(
    private val repository: TreatmentFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("treatmentId") private val treatmentId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("treatmentId") treatmentId: String?
        ): TreatmentFormViewModel
    }

    private val _state = MutableStateFlow(
        TreatmentFormState(
            isEditMode = !treatmentId.isNullOrBlank(),
            treatmentId = treatmentId
        )
    )
    val state: StateFlow<TreatmentFormState> = _state

    private val _eventChannel = Channel<TreatmentFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    fun onAction(action: TreatmentFormAction) {
        when (action) {
            TreatmentFormAction.TryLoadAgain -> loadInitialData()
            is TreatmentFormAction.OnTabSelected -> {
                _state.update { it.copy(currentTab = action.tab) }
            }
            TreatmentFormAction.SaveTreatment -> saveTreatments()
            is TreatmentFormAction.ToggleSaveDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }

            // BottomSheet Productos
            TreatmentFormAction.OnOpenProductSheet -> handleOpenProductSheet()
            TreatmentFormAction.OnDismissProductSheet -> {
                _state.update { it.copy(isProductSheetOpen = false) }
            }
            is TreatmentFormAction.OnProductSearchQueryChange -> {
                _state.update { it.copy(productSearchQuery = action.query) }
                debounceSearch { filterProducts(action.query) }
            }
            is TreatmentFormAction.OnToggleSelectProduct -> toggleSelectProduct(action.product)
            TreatmentFormAction.OnConfirmProductSelection -> confirmProductSelection()

            // BottomSheet Servicios
            TreatmentFormAction.OnOpenServiceSheet -> handleOpenServiceSheet()
            TreatmentFormAction.OnDismissServiceSheet -> {
                _state.update { it.copy(isServiceSheetOpen = false) }
            }
            is TreatmentFormAction.OnServiceSearchQueryChange -> {
                _state.update { it.copy(serviceSearchQuery = action.query) }
                debounceSearch { filterServices(action.query) }
            }
            is TreatmentFormAction.OnToggleSelectService -> toggleSelectService(action.service)
            TreatmentFormAction.OnConfirmServiceSelection -> confirmServiceSelection()

            // Modificación de ítems seleccionados
            is TreatmentFormAction.OnIncrementProduct -> incrementProduct(action.productId)
            is TreatmentFormAction.OnDecrementProduct -> decrementProduct(action.productId)
            is TreatmentFormAction.OnRemoveProduct -> removeProduct(action.productId)

            is TreatmentFormAction.OnIncrementService -> incrementService(action.serviceId)
            is TreatmentFormAction.OnDecrementService -> decrementService(action.serviceId)
            is TreatmentFormAction.OnRemoveService -> removeService(action.serviceId)
        }
    }

    private fun loadInitialData() {
        _state.update { it.copy(isLoadingDataInitial = true) }
        viewModelScope.launch {
            repository.getConsultation(consultationId.orEmpty()).fold(
                onSuccess = { consultation ->
                    _state.update { it.copy(consultationWithDetails = consultation) }
                    loadCatalogsAndTreatments()
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(TreatmentFormEvent.ShowErrorSnackbar("No se pudo cargar la información de la consulta."))
                }
            )
        }
    }

    private fun loadCatalogsAndTreatments() {
        viewModelScope.launch {
            val productsResult = repository.getActiveProductsWithDetails()
            val servicesResult = repository.getActiveServicesWithDetails()

            if (productsResult.isFailure || servicesResult.isFailure) {
                _state.update { it.copy(isLoadingDataInitial = false) }
                _eventChannel.send(TreatmentFormEvent.ShowErrorSnackbar("Error al sincronizar productos y servicios."))
                return@launch
            }

            val products = productsResult.getOrDefault(emptyList())
            val services = servicesResult.getOrDefault(emptyList())

            _state.update {
                it.copy(
                    productsWithDetails = products,
                    servicesWithDetails = services,
                    isSuccessGetData = true
                )
            }

            // OPTIMIZACIÓN: Solo consultamos la base de datos si venimos en modo edición
            if (_state.value.isEditMode) {
                loadExistingTreatments(products, services)
            } else {
                // Modo creación: no consumimos recursos consultando tratamientos inexistentes
                _state.update { it.copy(isLoadingDataInitial = false) }
            }
        }
    }

    private fun loadExistingTreatments(
        products: List<ProductWithDetailsModel>,
        services: List<ServiceWithDetailsModel>
    ) {
        viewModelScope.launch {
            repository.getTreatmentsByConsultationId(consultationId.orEmpty()).fold(
                onSuccess = { existingList ->
                    val selectedProducts = existingList.mapNotNull { treatmentWithDetails ->
                        val prod = treatmentWithDetails.product
                            ?: products.find { it.product.id == treatmentWithDetails.treatment.productId }
                        prod?.let {
                            SelectedTreatmentProduct(
                                productWithDetails = it,
                                quantity = treatmentWithDetails.treatment.quantity.toInt().coerceAtLeast(1)
                            )
                        }
                    }

                    val selectedServices = existingList.mapNotNull { treatmentWithDetails ->
                        val serv = treatmentWithDetails.service
                            ?: services.find { it.service.id == treatmentWithDetails.treatment.serviceId }
                        serv?.let {
                            SelectedTreatmentService(
                                serviceWithDetails = it,
                                quantity = treatmentWithDetails.treatment.quantity.toInt().coerceAtLeast(1)
                            )
                        }
                    }

                    val inputs = TreatmentFormInputsState(
                        selectedProducts = selectedProducts,
                        selectedServices = selectedServices
                    )

                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingTreatmentsWithDetails = existingList,
                            formInputState = inputs,
                            initialFormInputState = inputs,
                            isLoadingDataInitial = false
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(TreatmentFormEvent.ShowErrorSnackbar("No se pudieron cargar los tratamientos previos."))
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
                existingMap[item.product.id] ?: SelectedTreatmentProduct(productWithDetails = item, quantity = 1)
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
                existingMap[item.service.id] ?: SelectedTreatmentService(serviceWithDetails = item, quantity = 1)
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
                            _eventChannel.send(TreatmentFormEvent.ShowToast("Stock de suministros insuficiente para aumentar la cantidad."))
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
    private fun saveTreatments() {
        val s = _state.value
        if (!s.formInputState.isValid) return
        if (s.isEditMode) {
            updateTreatments()
        } else {
            registerTreatments()
        }
    }

    private fun buildTreatmentModels(): List<TreatmentModel> {
        val s = _state.value
        val productTreatments = s.formInputState.selectedProducts.map {
            TreatmentModel(
                consultationId = consultationId.orEmpty(),
                productId = it.productWithDetails.product.id,
                serviceId = null,
                quantity = it.quantity.toDouble(),
                status = Constants.ACTIVE_STATUS
            )
        }
        val serviceTreatments = s.formInputState.selectedServices.map {
            TreatmentModel(
                consultationId = consultationId.orEmpty(),
                productId = null,
                serviceId = it.serviceWithDetails.service.id,
                quantity = it.quantity.toDouble(),
                status = Constants.ACTIVE_STATUS
            )
        }
        return productTreatments + serviceTreatments
    }

    private fun registerTreatments() {
        val s = _state.value
        _state.update { it.copy(isLoadingSaveTreatment = true) }
        viewModelScope.launch {
            repository.saveTreatments(
                consultationId = consultationId.orEmpty(),
                treatments = buildTreatmentModels()
            ).fold(
                onSuccess = { savedList ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingTreatmentsWithDetails = savedList,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSaveTreatment = false
                        )
                    }
                    _eventChannel.send(TreatmentFormEvent.ShowSuccessSnackbar("Tratamiento registrado correctamente."))
                },
                onFailure = { error ->
                    Log.e("TreatmentFormViewModel", "Error al guardar tratamiento", error)
                    _state.update { it.copy(isLoadingSaveTreatment = false) }
                    _eventChannel.send(TreatmentFormEvent.ShowErrorSnackbar("Error al guardar el tratamiento."))
                }
            )
        }
    }

    private fun updateTreatments() {
        val s = _state.value
        _state.update { it.copy(isLoadingUpdateTreatment = true) }
        viewModelScope.launch {
            repository.updateTreatments(
                consultationId = consultationId.orEmpty(),
                treatments = buildTreatmentModels()
            ).fold(
                onSuccess = { updatedList ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingTreatmentsWithDetails = updatedList,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingUpdateTreatment = false
                        )
                    }
                    _eventChannel.send(TreatmentFormEvent.ShowSuccessSnackbar("Tratamiento actualizado correctamente."))
                },
                onFailure = { error ->
                    Log.e("TreatmentFormViewModel", "Error al actualizar tratamiento", error)
                    _state.update { it.copy(isLoadingUpdateTreatment = false) }
                    _eventChannel.send(TreatmentFormEvent.ShowErrorSnackbar("Error al actualizar el tratamiento."))
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