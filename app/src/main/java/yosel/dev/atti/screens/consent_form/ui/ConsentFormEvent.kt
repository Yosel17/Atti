package yosel.dev.atti.screens.consent_form.ui

sealed interface ConsentFormEvent {

    data class ShowErrorSnackbar(val message: String) : ConsentFormEvent
    data class ShowSuccessSnackbar(val message: String) : ConsentFormEvent
}