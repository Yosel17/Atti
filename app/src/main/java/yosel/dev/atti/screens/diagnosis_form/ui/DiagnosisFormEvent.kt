package yosel.dev.atti.screens.diagnosis_form.ui

sealed interface DiagnosisFormEvent {
    data class ShowErrorSnackbar(val message: String) : DiagnosisFormEvent
    data class ShowSuccessSnackbar(val message: String) : DiagnosisFormEvent
    data class ShowToast(val message: String) : DiagnosisFormEvent
}