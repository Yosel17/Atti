package yosel.dev.atti.screens.receipt_form.ui

import android.net.Uri
import yosel.dev.atti.screens.prescription_form.ui.PrescriptionFormEvent

sealed interface ReceiptFormEvent {
    data class ShowErrorSnackbar(val message: String) : ReceiptFormEvent
    data class ShowSuccessSnackbar(val message: String) : ReceiptFormEvent
    data class ShowToast(val message: String) : ReceiptFormEvent
    data object CreateDocument: ReceiptFormEvent
    data class ShowGenerateDocumentSnackbar(val message: String, val uri: Uri): ReceiptFormEvent
}