package yosel.dev.atti.screens.prescription_form.ui

import android.net.Uri

sealed interface PrescriptionFormEvent {
    data class ShowErrorSnackbar(val message: String) : PrescriptionFormEvent
    data class ShowSuccessSnackbar(val message: String) : PrescriptionFormEvent
    data class ShowToast(val message: String) : PrescriptionFormEvent
    data object CreateDocument: PrescriptionFormEvent
    data class ShowGenerateDocumentSnackbar(val message: String, val uri: Uri): PrescriptionFormEvent
}