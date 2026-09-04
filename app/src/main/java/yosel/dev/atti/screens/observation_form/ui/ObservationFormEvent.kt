package yosel.dev.atti.screens.observation_form.ui

sealed interface ObservationFormEvent {
    data class ShowErrorSnackbar(val message: String) : ObservationFormEvent
    data class ShowSuccessSnackbar(val message: String) : ObservationFormEvent
    data class ShowToast(val message: String) : ObservationFormEvent
}