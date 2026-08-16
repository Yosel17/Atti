package yosel.dev.atti.screens.service_form.ui

sealed interface ServiceFormEvent {
    data class ShowErrorSnackbar(val message: String) : ServiceFormEvent
    data class ShowSuccessSnackbar(val message: String) : ServiceFormEvent
    data class ShowToast(val message: String) : ServiceFormEvent
}