package yosel.dev.atti.screens.treatment_form.ui

sealed interface TreatmentFormEvent {
    data class ShowErrorSnackbar(val message: String) : TreatmentFormEvent
    data class ShowSuccessSnackbar(val message: String) : TreatmentFormEvent
    data class ShowToast(val message: String) : TreatmentFormEvent
}