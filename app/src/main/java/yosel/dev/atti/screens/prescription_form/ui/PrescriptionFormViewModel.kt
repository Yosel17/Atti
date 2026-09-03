package yosel.dev.atti.screens.prescription_form.ui

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
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.PrescriptionItemModel
import yosel.dev.atti.core.models.model.PrescriptionModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.prescription_form.domain.PrescriptionFormRepository
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = PrescriptionFormViewModel.Factory::class)
class PrescriptionFormViewModel @AssistedInject constructor(
    private val repository: PrescriptionFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("prescriptionId") private val prescriptionId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("prescriptionId") prescriptionId: String?
        ): PrescriptionFormViewModel
    }

    private val _state = MutableStateFlow(
        PrescriptionFormState(
            isEditMode = !prescriptionId.isNullOrBlank(),
            prescriptionId = prescriptionId
        )
    )
    val state: StateFlow<PrescriptionFormState> = _state

    private val _eventChannel = Channel<PrescriptionFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    fun onAction(action: PrescriptionFormAction) {
        when (action) {
            PrescriptionFormAction.TryLoadAgain -> loadInitialData()
            PrescriptionFormAction.SavePrescription -> savePrescription()
            is PrescriptionFormAction.ToggleSaveDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }
            is PrescriptionFormAction.OnGeneralNotesChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(generalNotes = action.notes)) }
            }

            // BottomSheet de Productos
            PrescriptionFormAction.OnOpenProductSheet -> handleOpenProductSheet()
            PrescriptionFormAction.OnDismissProductSheet -> {
                _state.update { it.copy(isProductSheetOpen = false) }
            }
            is PrescriptionFormAction.OnProductSearchQueryChange -> {
                _state.update { it.copy(productSearchQuery = action.query) }
                debounceSearch { filterProducts(action.query) }
            }
            is PrescriptionFormAction.OnToggleSelectProduct -> toggleSelectProduct(action.product)
            PrescriptionFormAction.OnConfirmProductSelection -> confirmProductSelection()

            // Presets Rápidos
            is PrescriptionFormAction.OnOpenPresetSheet -> handleOpenPresetSheet(action.targetItemId)
            PrescriptionFormAction.OnDismissPresetSheet -> {
                _state.update { it.copy(isPresetSheetOpen = false, targetPresetItemId = null) }
            }
            is PrescriptionFormAction.OnPresetSearchQueryChange -> {
                _state.update { it.copy(presetSearchQuery = action.query) }
                debounceSearch { filterPresets(action.query) }
            }
            is PrescriptionFormAction.OnSelectPreset -> applyPresetToItem(action.catalog)
            PrescriptionFormAction.OnShowAddPresetDialog -> {
                _state.update { it.copy(showAddPresetDialog = true) }
            }
            PrescriptionFormAction.OnDismissAddPresetDialog -> {
                _state.update { it.copy(showAddPresetDialog = false) }
            }
            is PrescriptionFormAction.OnSavePresetCatalog -> onSavePresetCatalog(action.name)

            // Producto fuera de inventario
            PrescriptionFormAction.OnOpenAddCustomProductDialog -> {
                _state.update { it.copy(showAddCustomProductDialog = true) }
            }
            PrescriptionFormAction.OnDismissAddCustomProductDialog -> {
                _state.update { it.copy(showAddCustomProductDialog = false) }
            }
            is PrescriptionFormAction.OnConfirmAddCustomProduct -> addCustomProduct(action.name, action.instructions)

            // Modificaciones de ítems
            is PrescriptionFormAction.OnInstructionsChange -> updateItemInstructions(action.itemId, action.instructions)
            is PrescriptionFormAction.OnIncrementQuantity -> incrementItem(action.itemId)
            is PrescriptionFormAction.OnDecrementQuantity -> decrementItem(action.itemId)
            is PrescriptionFormAction.OnRemoveItem -> removeItem(action.itemId)
        }
    }

    private fun loadInitialData() {
        _state.update { it.copy(isLoadingDataInitial = true) }
        viewModelScope.launch {
            repository.getConsultation(consultationId.orEmpty()).fold(
                onSuccess = { consultation ->
                    _state.update { it.copy(consultationWithDetails = consultation) }
                    loadCatalogsAndPrescriptions()
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(PrescriptionFormEvent.ShowErrorSnackbar("No se pudo cargar la información de la consulta."))
                }
            )
        }
    }

    private fun loadCatalogsAndPrescriptions() {
        viewModelScope.launch {
            val productsResult = repository.getActiveProductsWithDetails()
            val presetsResult = repository.getPresetCatalogs()

            if (productsResult.isFailure || presetsResult.isFailure) {
                _state.update { it.copy(isLoadingDataInitial = false) }
                _eventChannel.send(PrescriptionFormEvent.ShowErrorSnackbar("Error al sincronizar productos o presets."))
                return@launch
            }

            val products = productsResult.getOrDefault(emptyList())
            val presets = presetsResult.getOrDefault(emptyList()).sortedBy { it.name.lowercase() }

            _state.update {
                it.copy(
                    productsWithDetails = products,
                    presetCatalogs = presets,
                    filteredPresetCatalogs = presets,
                    isSuccessGetData = true
                )
            }

            if (_state.value.isEditMode) {
                loadExistingPrescription(products)
            } else {
                _state.update { it.copy(isLoadingDataInitial = false) }
            }
        }
    }

    private fun loadExistingPrescription(products: List<ProductWithDetailsModel>) {
        viewModelScope.launch {
            repository.getPrescriptionWithDetailsByConsultationId(consultationId.orEmpty()).fold(
                onSuccess = { prescriptionWithDetails ->
                    if (prescriptionWithDetails == null) {
                        _state.update { it.copy(isLoadingDataInitial = false) }
                        return@fold
                    }

                    val selectedItems = prescriptionWithDetails.items.map { itemWithDetails ->
                        val product = itemWithDetails.product
                            ?: products.find { it.product.id == itemWithDetails.item.productId }

                        SelectedPrescriptionItem(
                            localId = itemWithDetails.item.id.ifBlank { UUID.randomUUID().toString() },
                            productWithDetails = product,
                            customProductName = itemWithDetails.item.customProductName,
                            instructions = itemWithDetails.item.instructions,
                            quantity = itemWithDetails.item.quantity.toInt().coerceAtLeast(1)
                        )
                    }

                    val formState = PrescriptionFormInputsState(
                        selectedItems = selectedItems,
                        generalNotes = prescriptionWithDetails.prescription.generalNotes
                    )

                    _state.update {
                        it.copy(
                            isEditMode = true,
                            prescriptionId = prescriptionWithDetails.prescription.id,
                            existingPrescriptionWithDetails = prescriptionWithDetails,
                            formInputState = formState,
                            initialFormInputState = formState,
                            isLoadingDataInitial = false
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(PrescriptionFormEvent.ShowErrorSnackbar("No se pudo cargar la receta previa."))
                }
            )
        }
    }

    // --- BottomSheet de Selección de Productos ---
    private fun handleOpenProductSheet() {
        val currentSelectedIds = _state.value.formInputState.selectedItems
            .mapNotNull { it.productWithDetails?.product?.id }
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
        val existingInventoryMap = s.formInputState.selectedItems
            .filter { it.productWithDetails != null }
            .associateBy { it.productWithDetails!!.product.id }

        val customItems = s.formInputState.selectedItems.filter { it.isCustom }

        val newInventoryItems = s.productsWithDetails
            .filter { s.tempSelectedProductIds.contains(it.product.id) }
            .map { item ->
                existingInventoryMap[item.product.id] ?: SelectedPrescriptionItem(
                    productWithDetails = item,
                    quantity = 1
                )
            }

        _state.update {
            it.copy(
                isProductSheetOpen = false,
                formInputState = it.formInputState.copy(selectedItems = newInventoryItems + customItems)
            )
        }
    }

    // --- Presets Rápidos ---
    private fun handleOpenPresetSheet(targetItemId: String) {
        _state.update {
            it.copy(
                targetPresetItemId = targetItemId,
                presetSearchQuery = "",
                filteredPresetCatalogs = it.presetCatalogs,
                isPresetSheetOpen = true
            )
        }
    }

    private fun filterPresets(query: String) {
        val q = query.normalize()
        _state.update { s ->
            val filtered = if (q.isBlank()) s.presetCatalogs else s.presetCatalogs.filter { it.name.normalize().contains(q) }
            s.copy(filteredPresetCatalogs = filtered)
        }
    }

    private fun applyPresetToItem(preset: AppCatalogModel) {
        val targetId = _state.value.targetPresetItemId ?: return
        updateItemInstructions(targetId, preset.name)
        _state.update { it.copy(isPresetSheetOpen = false, targetPresetItemId = null) }
    }

    private fun onSavePresetCatalog(name: String) {
        _state.update { it.copy(isLoadingAddPreset = true) }
        viewModelScope.launch {
            val newCatalog = AppCatalogModel(
                id = 0,
                catalogTypeId = Constants.PRESETS_CATALOG_TYPE,
                name = name,
                description = "",
                isActive = true,
                createdAt = ""
            )
            repository.insertCatalog(newCatalog).fold(
                onSuccess = { inserted ->
                    val updatedPresets = (_state.value.presetCatalogs + inserted).sortedBy { it.name.lowercase() }
                    _state.update { s ->
                        s.copy(
                            presetCatalogs = updatedPresets,
                            filteredPresetCatalogs = updatedPresets,
                            isLoadingAddPreset = false,
                            showAddPresetDialog = false
                        )
                    }
                    _state.value.targetPresetItemId?.let { targetId ->
                        updateItemInstructions(targetId, inserted.name)
                        _state.update { it.copy(isPresetSheetOpen = false, targetPresetItemId = null) }
                    }
                    _eventChannel.send(PrescriptionFormEvent.ShowToast("Preset rápido agregado correctamente."))
                },
                onFailure = {
                    _state.update { it.copy(isLoadingAddPreset = false, showAddPresetDialog = false) }
                    _eventChannel.send(PrescriptionFormEvent.ShowToast("No se pudo agregar el preset."))
                }
            )
        }
    }

    // --- Productos fuera de inventario ---
    private fun addCustomProduct(name: String, instructions: String) {
        if (name.isBlank()) return
        val newItem = SelectedPrescriptionItem(
            customProductName = name.trim(),
            instructions = instructions.trim(),
            quantity = 1
        )
        _state.update { s ->
            s.copy(
                showAddCustomProductDialog = false,
                formInputState = s.formInputState.copy(selectedItems = s.formInputState.selectedItems + newItem)
            )
        }
    }

    // --- Modificación de ítems ---
    private fun updateItemInstructions(itemId: String, instructions: String) {
        _state.update { s ->
            val updated = s.formInputState.selectedItems.map {
                if (it.localId == itemId) it.copy(instructions = instructions) else it
            }
            s.copy(formInputState = s.formInputState.copy(selectedItems = updated))
        }
    }

    private fun incrementItem(itemId: String) {
        _state.update { s ->
            val updated = s.formInputState.selectedItems.map {
                if (it.localId == itemId) {
                    if (it.quantity < it.maxStock) it.copy(quantity = it.quantity + 1) else it
                } else it
            }
            s.copy(formInputState = s.formInputState.copy(selectedItems = updated))
        }
    }

    private fun decrementItem(itemId: String) {
        _state.update { s ->
            val updated = s.formInputState.selectedItems.map {
                if (it.localId == itemId && it.quantity > 1) it.copy(quantity = it.quantity - 1) else it
            }
            s.copy(formInputState = s.formInputState.copy(selectedItems = updated))
        }
    }

    private fun removeItem(itemId: String) {
        _state.update { s ->
            val updated = s.formInputState.selectedItems.filterNot { it.localId == itemId }
            s.copy(formInputState = s.formInputState.copy(selectedItems = updated))
        }
    }

    // --- Guardado y Actualización ---
    private fun savePrescription() {
        val s = _state.value
        if (!s.formInputState.isValid) return
        if (s.isEditMode) {
            updateExistingPrescription()
        } else {
            registerNewPrescription()
        }
    }

    private fun registerNewPrescription() {
        val s = _state.value
        _state.update { it.copy(isLoadingSavePrescription = true) }
        viewModelScope.launch {
            val prescription = PrescriptionModel(
                consultationId = consultationId.orEmpty(),
                generalNotes = s.formInputState.generalNotes.trim(),
                status = Constants.ACTIVE_STATUS
            )
            val items = s.formInputState.selectedItems.map {
                PrescriptionItemModel(
                    productId = it.productWithDetails?.product?.id,
                    customProductName = if (it.isCustom) it.customProductName.trim() else "",
                    instructions = it.instructions.trim(),
                    quantity = it.quantity.toDouble(),
                    status = Constants.ACTIVE_STATUS
                )
            }

            repository.savePrescription(
                consultationId = consultationId.orEmpty(),
                prescription = prescription,
                items = items
            ).fold(
                onSuccess = { savedWithDetails ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            prescriptionId = savedWithDetails.prescription.id,
                            existingPrescriptionWithDetails = savedWithDetails,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSavePrescription = false
                        )
                    }
                    _eventChannel.send(PrescriptionFormEvent.ShowSuccessSnackbar("Receta médica registrada exitosamente."))
                },
                onFailure = { error ->
                    Log.e("PrescriptionFormVM", "Error al registrar receta", error)
                    _state.update { it.copy(isLoadingSavePrescription = false) }
                    _eventChannel.send(PrescriptionFormEvent.ShowErrorSnackbar("No se pudo guardar la receta médica."))
                }
            )
        }
    }

    private fun updateExistingPrescription() {
        val s = _state.value
        val existingId = s.prescriptionId ?: s.existingPrescriptionWithDetails?.prescription?.id ?: return
        _state.update { it.copy(isLoadingUpdatePrescription = true) }
        viewModelScope.launch {
            val prescription = PrescriptionModel(
                id = existingId,
                consultationId = consultationId.orEmpty(),
                generalNotes = s.formInputState.generalNotes.trim(),
                createdAt = s.existingPrescriptionWithDetails?.prescription?.createdAt.orEmpty(),
                status = Constants.ACTIVE_STATUS
            )
            val items = s.formInputState.selectedItems.map {
                PrescriptionItemModel(
                    prescriptionId = existingId,
                    productId = it.productWithDetails?.product?.id,
                    customProductName = if (it.isCustom) it.customProductName.trim() else "",
                    instructions = it.instructions.trim(),
                    quantity = it.quantity.toDouble(),
                    status = Constants.ACTIVE_STATUS
                )
            }

            repository.updatePrescription(
                consultationId = consultationId.orEmpty(),
                prescription = prescription,
                items = items
            ).fold(
                onSuccess = { updatedWithDetails ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingPrescriptionWithDetails = updatedWithDetails,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingUpdatePrescription = false
                        )
                    }
                    _eventChannel.send(PrescriptionFormEvent.ShowSuccessSnackbar("Receta médica actualizada correctamente."))
                },
                onFailure = { error ->
                    Log.e("PrescriptionFormVM", "Error al actualizar receta", error)
                    _state.update { it.copy(isLoadingUpdatePrescription = false) }
                    _eventChannel.send(PrescriptionFormEvent.ShowErrorSnackbar("No se pudo actualizar la receta médica."))
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