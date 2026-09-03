package yosel.dev.atti.screens.prescription_form.ui

sealed interface PrescriptionFormEvent {
    data class ShowErrorSnackbar(val message: String) : PrescriptionFormEvent
    data class ShowSuccessSnackbar(val message: String) : PrescriptionFormEvent
    data class ShowToast(val message: String) : PrescriptionFormEvent
}