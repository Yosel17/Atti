package yosel.dev.atti.screens.treatment_form.ui

import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

sealed interface TreatmentFormAction {
    data object TryLoadAgain : TreatmentFormAction
    data class OnTabSelected(val tab: TreatmentTab) : TreatmentFormAction
    data object SaveTreatment : TreatmentFormAction
    data class ToggleSaveDialog(val show: Boolean) : TreatmentFormAction

    // BottomSheet Productos
    data object OnOpenProductSheet : TreatmentFormAction
    data object OnDismissProductSheet : TreatmentFormAction
    data class OnProductSearchQueryChange(val query: String) : TreatmentFormAction
    data class OnToggleSelectProduct(val product: ProductWithDetailsModel) : TreatmentFormAction
    data object OnConfirmProductSelection : TreatmentFormAction

    // BottomSheet Servicios
    data object OnOpenServiceSheet : TreatmentFormAction
    data object OnDismissServiceSheet : TreatmentFormAction
    data class OnServiceSearchQueryChange(val query: String) : TreatmentFormAction
    data class OnToggleSelectService(val service: ServiceWithDetailsModel) : TreatmentFormAction
    data object OnConfirmServiceSelection : TreatmentFormAction

    // Contadores e ítems seleccionados
    data class OnIncrementProduct(val productId: String) : TreatmentFormAction
    data class OnDecrementProduct(val productId: String) : TreatmentFormAction
    data class OnRemoveProduct(val productId: String) : TreatmentFormAction

    data class OnIncrementService(val serviceId: String) : TreatmentFormAction
    data class OnDecrementService(val serviceId: String) : TreatmentFormAction
    data class OnRemoveService(val serviceId: String) : TreatmentFormAction
}