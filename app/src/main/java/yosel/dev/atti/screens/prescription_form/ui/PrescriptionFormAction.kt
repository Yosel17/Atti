package yosel.dev.atti.screens.prescription_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel

sealed interface PrescriptionFormAction {
    data object TryLoadAgain : PrescriptionFormAction
    data object SavePrescription : PrescriptionFormAction
    data class ToggleSaveDialog(val show: Boolean) : PrescriptionFormAction

    // BottomSheet Productos (Inventario)
    data object OnOpenProductSheet : PrescriptionFormAction
    data object OnDismissProductSheet : PrescriptionFormAction
    data class OnProductSearchQueryChange(val query: String) : PrescriptionFormAction
    data class OnToggleSelectProduct(val product: ProductWithDetailsModel) : PrescriptionFormAction
    data object OnConfirmProductSelection : PrescriptionFormAction

    // Presets Rápidos
    data class OnOpenPresetSheet(val targetItemId: String) : PrescriptionFormAction
    data object OnDismissPresetSheet : PrescriptionFormAction
    data class OnPresetSearchQueryChange(val query: String) : PrescriptionFormAction
    data class OnSelectPreset(val catalog: AppCatalogModel) : PrescriptionFormAction
    data object OnShowAddPresetDialog : PrescriptionFormAction
    data object OnDismissAddPresetDialog : PrescriptionFormAction
    data class OnSavePresetCatalog(val name: String) : PrescriptionFormAction

    // Productos Fuera de Inventario
    data object OnOpenAddCustomProductDialog : PrescriptionFormAction
    data object OnDismissAddCustomProductDialog : PrescriptionFormAction
    data class OnConfirmAddCustomProduct(val name: String, val instructions: String) : PrescriptionFormAction

    // Edición de ítems
    data class OnInstructionsChange(val itemId: String, val instructions: String) : PrescriptionFormAction
    data class OnIncrementQuantity(val itemId: String) : PrescriptionFormAction
    data class OnDecrementQuantity(val itemId: String) : PrescriptionFormAction
    data class OnRemoveItem(val itemId: String) : PrescriptionFormAction

    // Notas generales
    data class OnGeneralNotesChange(val notes: String) : PrescriptionFormAction
}