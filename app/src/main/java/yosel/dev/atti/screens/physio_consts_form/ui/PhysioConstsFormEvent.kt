package yosel.dev.atti.screens.physio_consts_form.ui

sealed interface PhysioConstsFormEvent {
    data class ShowErrorSnackbar(val message: String) : PhysioConstsFormEvent
    data class ShowSuccessSnackbar(val message: String) : PhysioConstsFormEvent
    data class ShowToast(val message: String) : PhysioConstsFormEvent
}