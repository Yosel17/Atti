package yosel.dev.atti.screens.receipt_form.ui

import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

sealed interface ReceiptFormAction {
    data object TryLoadAgain : ReceiptFormAction
    data class OnTabSelected(val tab: ReceiptTab) : ReceiptFormAction
    data class OnCustomerNameChange(val name: String) : ReceiptFormAction
    data object SaveReceipt : ReceiptFormAction
    data class ToggleSaveDialog(val show: Boolean) : ReceiptFormAction

    // BottomSheet Productos
    data object OnOpenProductSheet : ReceiptFormAction
    data object OnDismissProductSheet : ReceiptFormAction
    data class OnProductSearchQueryChange(val query: String) : ReceiptFormAction
    data class OnToggleSelectProduct(val product: ProductWithDetailsModel) : ReceiptFormAction
    data object OnConfirmProductSelection : ReceiptFormAction

    // BottomSheet Servicios
    data object OnOpenServiceSheet : ReceiptFormAction
    data object OnDismissServiceSheet : ReceiptFormAction
    data class OnServiceSearchQueryChange(val query: String) : ReceiptFormAction
    data class OnToggleSelectService(val service: ServiceWithDetailsModel) : ReceiptFormAction
    data object OnConfirmServiceSelection : ReceiptFormAction

    // Items Seleccionados
    data class OnIncrementProduct(val productId: String) : ReceiptFormAction
    data class OnDecrementProduct(val productId: String) : ReceiptFormAction
    data class OnRemoveProduct(val productId: String) : ReceiptFormAction
    data class OnIncrementService(val serviceId: String) : ReceiptFormAction
    data class OnDecrementService(val serviceId: String) : ReceiptFormAction
    data class OnRemoveService(val serviceId: String) : ReceiptFormAction
}