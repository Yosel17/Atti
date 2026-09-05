package yosel.dev.atti.screens.receipt_form.ui

sealed interface ReceiptFormEvent {
    data class ShowErrorSnackbar(val message: String) : ReceiptFormEvent
    data class ShowSuccessSnackbar(val message: String) : ReceiptFormEvent
    data class ShowToast(val message: String) : ReceiptFormEvent
}