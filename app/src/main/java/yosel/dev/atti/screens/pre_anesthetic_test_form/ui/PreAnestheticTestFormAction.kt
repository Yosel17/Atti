package yosel.dev.atti.screens.pre_anesthetic_test_form.ui

import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

sealed interface PreAnestheticTestFormAction {
    data object TryLoadAgain : PreAnestheticTestFormAction
    data class OnTabSelected(val tab: PreAnestheticTestTab) : PreAnestheticTestFormAction
    data object SavePreAnestheticTest : PreAnestheticTestFormAction
    data class ToggleSaveDialog(val show: Boolean) : PreAnestheticTestFormAction
    data class OnNotesChange(val notes: String) : PreAnestheticTestFormAction

    // BottomSheet Productos
    data object OnOpenProductSheet : PreAnestheticTestFormAction
    data object OnDismissProductSheet : PreAnestheticTestFormAction
    data class OnProductSearchQueryChange(val query: String) : PreAnestheticTestFormAction
    data class OnToggleSelectProduct(val product: ProductWithDetailsModel) : PreAnestheticTestFormAction
    data object OnConfirmProductSelection : PreAnestheticTestFormAction

    // BottomSheet Servicios
    data object OnOpenServiceSheet : PreAnestheticTestFormAction
    data object OnDismissServiceSheet : PreAnestheticTestFormAction
    data class OnServiceSearchQueryChange(val query: String) : PreAnestheticTestFormAction
    data class OnToggleSelectService(val service: ServiceWithDetailsModel) : PreAnestheticTestFormAction
    data object OnConfirmServiceSelection : PreAnestheticTestFormAction

    // Contadores e ítems seleccionados
    data class OnIncrementProduct(val productId: String) : PreAnestheticTestFormAction
    data class OnDecrementProduct(val productId: String) : PreAnestheticTestFormAction
    data class OnRemoveProduct(val productId: String) : PreAnestheticTestFormAction

    data class OnIncrementService(val serviceId: String) : PreAnestheticTestFormAction
    data class OnDecrementService(val serviceId: String) : PreAnestheticTestFormAction
    data class OnRemoveService(val serviceId: String) : PreAnestheticTestFormAction
}
