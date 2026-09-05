package yosel.dev.atti.screens.receipt_form.ui

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
import yosel.dev.atti.core.models.model.ReceiptItemModel
import yosel.dev.atti.core.models.model.ReceiptModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.receipt_form.domain.ReceiptFormRepository
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = ReceiptFormViewModel.Factory::class)
class ReceiptFormViewModel @AssistedInject constructor(
    private val repository: ReceiptFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("receiptId") private val receiptId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("receiptId") receiptId: Long?
        ): ReceiptFormViewModel
    }

    private val _state = MutableStateFlow(
        ReceiptFormState(
            isEditMode = receiptId != null,
            receiptId = receiptId,
            consultationId = consultationId,
            hasConsultation = !consultationId.isNullOrBlank()
        )
    )
    val state: StateFlow<ReceiptFormState> = _state

    private val _eventChannel = Channel<ReceiptFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    fun onAction(action: ReceiptFormAction) {
        when (action) {
            ReceiptFormAction.TryLoadAgain -> loadInitialData()
            is ReceiptFormAction.OnTabSelected -> {
                _state.update { it.copy(currentTab = action.tab) }
            }
            is ReceiptFormAction.OnCustomerNameChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(customerName = action.name)) }
            }
            ReceiptFormAction.SaveReceipt -> saveReceipt()
            is ReceiptFormAction.ToggleSaveDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }
            // BottomSheet Productos
            ReceiptFormAction.OnOpenProductSheet -> handleOpenProductSheet()
            ReceiptFormAction.OnDismissProductSheet -> {
                _state.update { it.copy(isProductSheetOpen = false) }
            }
            is ReceiptFormAction.OnProductSearchQueryChange -> {
                _state.update { it.copy(productSearchQuery = action.query) }
                debounceSearch { filterProducts(action.query) }
            }
            is ReceiptFormAction.OnToggleSelectProduct -> toggleSelectProduct(action.product)
            ReceiptFormAction.OnConfirmProductSelection -> confirmProductSelection()
            // BottomSheet Servicios
            ReceiptFormAction.OnOpenServiceSheet -> handleOpenServiceSheet()
            ReceiptFormAction.OnDismissServiceSheet -> {
                _state.update { it.copy(isServiceSheetOpen = false) }
            }
            is ReceiptFormAction.OnServiceSearchQueryChange -> {
                _state.update { it.copy(serviceSearchQuery = action.query) }
                debounceSearch { filterServices(action.query) }
            }
            is ReceiptFormAction.OnToggleSelectService -> toggleSelectService(action.service)
            ReceiptFormAction.OnConfirmServiceSelection -> confirmServiceSelection()
            // Incrementos / Decrementos / Eliminación
            is ReceiptFormAction.OnIncrementProduct -> incrementProduct(action.productId)
            is ReceiptFormAction.OnDecrementProduct -> decrementProduct(action.productId)
            is ReceiptFormAction.OnRemoveProduct -> removeProduct(action.productId)
            is ReceiptFormAction.OnIncrementService -> incrementService(action.serviceId)
            is ReceiptFormAction.OnDecrementService -> decrementService(action.serviceId)
            is ReceiptFormAction.OnRemoveService -> removeService(action.serviceId)
        }
    }

    private fun loadInitialData() {
        _state.update { it.copy(isLoadingDataInitial = true) }
        viewModelScope.launch {
            if (!consultationId.isNullOrBlank()) {
                repository.getConsultation(consultationId).fold(
                    onSuccess = { consultation ->
                        _state.update { it.copy(consultationWithDetails = consultation) }
                        loadCatalogsAndContent()
                    },
                    onFailure = {
                        _state.update { it.copy(isLoadingDataInitial = false) }
                        _eventChannel.send(ReceiptFormEvent.ShowErrorSnackbar("No se pudo cargar la información de la consulta."))
                    }
                )
            } else {
                loadCatalogsAndContent()
            }
        }
    }

    private fun loadCatalogsAndContent() {
        viewModelScope.launch {
            val productsResult = repository.getActiveProductsWithDetails()
            val servicesResult = repository.getActiveServicesWithDetails()

            if (productsResult.isFailure || servicesResult.isFailure) {
                _state.update { it.copy(isLoadingDataInitial = false) }
                _eventChannel.send(ReceiptFormEvent.ShowErrorSnackbar("Error al sincronizar productos y servicios."))
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

            if (_state.value.isEditMode) {
                loadExistingReceipt(products, services)
            } else if (!consultationId.isNullOrBlank()) {
                loadTreatmentsAndPrescriptionsForReceipt(products, services)
            } else {
                _state.update { it.copy(isLoadingDataInitial = false) }
            }
        }
    }

    private fun loadExistingReceipt(
        products: List<ProductWithDetailsModel>,
        services: List<ServiceWithDetailsModel>
    ) {
        viewModelScope.launch {
            val result = if (receiptId != null) {
                repository.getReceiptWithDetailsById(receiptId)
            } else {
                repository.getReceiptWithDetailsByConsultationId(consultationId.orEmpty())
            }

            result.fold(
                onSuccess = { receiptWithDetails ->
                    if (receiptWithDetails == null) {
                        _state.update { it.copy(isLoadingDataInitial = false) }
                        return@fold
                    }

                    val selectedProducts = receiptWithDetails.items.mapNotNull { itemWithDetails ->
                        if (itemWithDetails.item.productId == null) return@mapNotNull null
                        val prod = itemWithDetails.product
                            ?: products.find { it.product.id == itemWithDetails.item.productId }
                        prod?.let {
                            SelectedReceiptProduct(
                                productWithDetails = it,
                                quantity = itemWithDetails.item.quantity.toInt().coerceAtLeast(1)
                            )
                        }
                    }

                    val selectedServices = receiptWithDetails.items.mapNotNull { itemWithDetails ->
                        if (itemWithDetails.item.serviceId == null) return@mapNotNull null
                        val serv = itemWithDetails.service
                            ?: services.find { it.service.id == itemWithDetails.item.serviceId }
                        serv?.let {
                            SelectedReceiptService(
                                serviceWithDetails = it,
                                quantity = itemWithDetails.item.quantity.toInt().coerceAtLeast(1)
                            )
                        }
                    }

                    val inputs = ReceiptFormInputsState(
                        customerName = receiptWithDetails.receipt.customerName,
                        selectedProducts = selectedProducts,
                        selectedServices = selectedServices
                    )

                    _state.update {
                        it.copy(
                            isEditMode = true,
                            receiptId = receiptWithDetails.receipt.id,
                            existingReceiptWithDetails = receiptWithDetails,
                            formInputState = inputs,
                            initialFormInputState = inputs,
                            isLoadingDataInitial = false
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(ReceiptFormEvent.ShowErrorSnackbar("No se pudo cargar el recibo previo."))
                }
            )
        }
    }

    private fun loadTreatmentsAndPrescriptionsForReceipt(
        products: List<ProductWithDetailsModel>,
        services: List<ServiceWithDetailsModel>
    ) {
        viewModelScope.launch {
            val cId = consultationId.orEmpty()
            val treatmentsResult = repository.getTreatmentsByConsultationId(cId)
            val prescriptionsResult = repository.getPrescriptionItemsByConsultationId(cId)

            val treatments = treatmentsResult.getOrDefault(emptyList())
            val prescriptionItems = prescriptionsResult.getOrDefault(emptyList())

            // Agrupación de productos (Tratamientos + Receta)
            val productQuantities = mutableMapOf<String, Int>()
            treatments.filter { !it.productId.isNullOrBlank() }.forEach { t ->
                val id = t.productId!!
                productQuantities[id] = (productQuantities[id] ?: 0) + t.quantity.toInt().coerceAtLeast(1)
            }
            prescriptionItems.filter { !it.productId.isNullOrBlank() }.forEach { pi ->
                val id = pi.productId!!
                productQuantities[id] = (productQuantities[id] ?: 0) + pi.quantity.toInt().coerceAtLeast(1)
            }

            val initialProducts = productQuantities.mapNotNull { (productId, qty) ->
                val prod = products.find { it.product.id == productId }
                prod?.let {
                    SelectedReceiptProduct(
                        productWithDetails = it,
                        quantity = qty.coerceAtMost(it.product.stock).coerceAtLeast(1)
                    )
                }
            }

            // Agrupación de servicios (Tratamientos)
            val serviceQuantities = mutableMapOf<String, Int>()
            treatments.filter { !it.serviceId.isNullOrBlank() }.forEach { t ->
                val id = t.serviceId!!
                serviceQuantities[id] = (serviceQuantities[id] ?: 0) + t.quantity.toInt().coerceAtLeast(1)
            }

            val initialServices = serviceQuantities.mapNotNull { (serviceId, qty) ->
                val serv = services.find { it.service.id == serviceId }
                serv?.let {
                    SelectedReceiptService(
                        serviceWithDetails = it,
                        quantity = qty.coerceAtLeast(1)
                    )
                }
            }

            val inputs = ReceiptFormInputsState(
                customerName = _state.value.consultationWithDetails.patientWithDetails.patient.name,
                selectedProducts = initialProducts,
                selectedServices = initialServices
            )

            _state.update {
                it.copy(
                    formInputState = inputs,
                    initialFormInputState = inputs,
                    isLoadingDataInitial = false
                )
            }
        }
    }

    // --- BottomSheet Productos ---
    private fun handleOpenProductSheet() {
        val currentSelectedIds = _state.value.formInputState.selectedProducts
            .map { it.productWithDetails.product.id }
            .toSet()
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
            if (normalizedQuery.isBlank()) true else {
                item.product.commercialName.normalize().contains(normalizedQuery) ||
                        item.product.brand.normalize().contains(normalizedQuery)
            }
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
                existingMap[item.product.id] ?: SelectedReceiptProduct(productWithDetails = item, quantity = 1)
            }
        _state.update {
            it.copy(
                isProductSheetOpen = false,
                formInputState = it.formInputState.copy(selectedProducts = newSelection)
            )
        }
    }

    // --- BottomSheet Servicios ---
    private fun handleOpenServiceSheet() {
        val currentSelectedIds = _state.value.formInputState.selectedServices
            .map { it.serviceWithDetails.service.id }
            .toSet()
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
                existingMap[item.service.id] ?: SelectedReceiptService(serviceWithDetails = item, quantity = 1)
            }
        _state.update {
            it.copy(
                isServiceSheetOpen = false,
                formInputState = it.formInputState.copy(selectedServices = newSelection)
            )
        }
    }

    // --- Modificadores de Cantidad ---
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
                            _eventChannel.send(ReceiptFormEvent.ShowToast("Stock de suministros insuficiente para el servicio."))
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

    private fun isServiceSelectable(service: ServiceWithDetailsModel): Boolean {
        if (service.supplies.isEmpty()) return true
        return service.supplies.none { it.product.product.stock <= 0 }
    }

    private fun canIncrementService(service: ServiceWithDetailsModel, currentQuantity: Int): Boolean {
        if (service.supplies.isEmpty()) return true
        val target = currentQuantity + 1
        return service.supplies.all { supplyItem ->
            val required = supplyItem.supply.quantityRequired * target
            supplyItem.product.product.stock >= required
        }
    }

    // --- Persistencia ---
    private fun saveReceipt() {
        val s = _state.value
        if (!s.formInputState.isValid) return
        if (s.isEditMode) {
            updateReceiptInternal()
        } else {
            registerReceiptInternal()
        }
    }

    private fun buildReceiptModel(receiptId: String = ""): ReceiptModel {
        val s = _state.value
        return ReceiptModel(
            id = receiptId,
            consultationId = consultationId?.takeIf { it.isNotBlank() },
            customerName = s.formInputState.customerName.trim(),
            subtotal = s.formInputState.subtotalAmount,
            discount = 0.0,
            tax = 0.0,
            total = s.formInputState.totalAmount,
            notes = "",
            createdAt = s.existingReceiptWithDetails?.receipt?.createdAt.orEmpty(),
            status = Constants.ACTIVE_STATUS
        )
    }

    private fun buildReceiptItemModels(receiptId: String = ""): List<ReceiptItemModel> {
        val s = _state.value
        val productItems = s.formInputState.selectedProducts.map {
            ReceiptItemModel(
                receiptId = receiptId,
                productId = it.productWithDetails.product.id,
                serviceId = null,
                quantity = it.quantity.toDouble(),
                unitPrice = it.productWithDetails.product.salePrice,
                subtotal = it.productWithDetails.product.salePrice * it.quantity,
                status = Constants.ACTIVE_STATUS
            )
        }
        val serviceItems = s.formInputState.selectedServices.map {
            ReceiptItemModel(
                receiptId = receiptId,
                productId = null,
                serviceId = it.serviceWithDetails.service.id,
                quantity = it.quantity.toDouble(),
                unitPrice = it.serviceWithDetails.service.salePrice,
                subtotal = it.serviceWithDetails.service.salePrice * it.quantity,
                status = Constants.ACTIVE_STATUS
            )
        }
        return productItems + serviceItems
    }

    private fun registerReceiptInternal() {
        val s = _state.value
        _state.update { it.copy(isLoadingSaveReceipt = true) }
        viewModelScope.launch {
            val receipt = buildReceiptModel()
            val items = buildReceiptItemModels()

            repository.saveReceipt(
                consultationId = consultationId,
                receipt = receipt,
                items = items
            ).fold(
                onSuccess = { savedReceiptWithDetails ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            receiptId = savedReceiptWithDetails.receipt.id,
                            existingReceiptWithDetails = savedReceiptWithDetails,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSaveReceipt = false
                        )
                    }
                    _eventChannel.send(ReceiptFormEvent.ShowSuccessSnackbar("Recibo guardado exitosamente."))
                },
                onFailure = { error ->
                    Log.e("ReceiptFormViewModel", "Error al guardar recibo", error)
                    _state.update { it.copy(isLoadingSaveReceipt = false) }
                    _eventChannel.send(ReceiptFormEvent.ShowErrorSnackbar("Error al registrar el recibo."))
                }
            )
        }
    }

    private fun updateReceiptInternal() {
        val s = _state.value
        val existingId = s.receiptId ?: s.existingReceiptWithDetails?.receipt?.id ?: return
        _state.update { it.copy(isLoadingUpdateReceipt = true) }
        viewModelScope.launch {
            val receipt = buildReceiptModel(existingId)
            val items = buildReceiptItemModels(existingId)

            repository.updateReceipt(receipt, items).fold(
                onSuccess = { updatedReceiptWithDetails ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingReceiptWithDetails = updatedReceiptWithDetails,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingUpdateReceipt = false
                        )
                    }
                    _eventChannel.send(ReceiptFormEvent.ShowSuccessSnackbar("Recibo actualizado correctamente."))
                },
                onFailure = { error ->
                    Log.e("ReceiptFormViewModel", "Error al actualizar recibo", error)
                    _state.update { it.copy(isLoadingUpdateReceipt = false) }
                    _eventChannel.send(ReceiptFormEvent.ShowErrorSnackbar("Error al actualizar el recibo."))
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