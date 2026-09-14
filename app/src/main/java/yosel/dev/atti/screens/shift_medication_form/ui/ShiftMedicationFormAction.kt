package yosel.dev.atti.screens.shift_medication_form.ui

import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

sealed interface ShiftMedicationFormAction {
    data object TryLoadAgain : ShiftMedicationFormAction
    data class OnTabSelected(val tab: ShiftMedicationTab) : ShiftMedicationFormAction
    data object SaveShiftMedication : ShiftMedicationFormAction
    data class ToggleSaveDialog(val show: Boolean) : ShiftMedicationFormAction
    data class OnNotesChange(val notes: String) : ShiftMedicationFormAction

    // BottomSheet Productos
    data object OnOpenProductSheet : ShiftMedicationFormAction
    data object OnDismissProductSheet : ShiftMedicationFormAction
    data class OnProductSearchQueryChange(val query: String) : ShiftMedicationFormAction
    data class OnToggleSelectProduct(val product: ProductWithDetailsModel) : ShiftMedicationFormAction
    data object OnConfirmProductSelection : ShiftMedicationFormAction

    // BottomSheet Servicios
    data object OnOpenServiceSheet : ShiftMedicationFormAction
    data object OnDismissServiceSheet : ShiftMedicationFormAction
    data class OnServiceSearchQueryChange(val query: String) : ShiftMedicationFormAction
    data class OnToggleSelectService(val service: ServiceWithDetailsModel) : ShiftMedicationFormAction
    data object OnConfirmServiceSelection : ShiftMedicationFormAction

    // Contadores e ítems seleccionados
    data class OnIncrementProduct(val productId: String) : ShiftMedicationFormAction
    data class OnDecrementProduct(val productId: String) : ShiftMedicationFormAction
    data class OnRemoveProduct(val productId: String) : ShiftMedicationFormAction

    data class OnIncrementService(val serviceId: String) : ShiftMedicationFormAction
    data class OnDecrementService(val serviceId: String) : ShiftMedicationFormAction
    data class OnRemoveService(val serviceId: String) : ShiftMedicationFormAction
}
