package yosel.dev.atti.screens.fasting_form.ui

sealed class FastingFormEvent {
    data class ShowErrorSnackbar(val message: String) : FastingFormEvent()
    data class ShowSuccessSnackbar(val message: String) : FastingFormEvent()
    data class ShowToast(val message: String) : FastingFormEvent()
}