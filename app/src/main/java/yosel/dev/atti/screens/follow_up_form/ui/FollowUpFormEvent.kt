package yosel.dev.atti.screens.follow_up_form.ui

sealed interface FollowUpFormEvent {
    data class ShowErrorSnackbar(val message: String) : FollowUpFormEvent
    data class ShowSuccessSnackbar(val message: String) : FollowUpFormEvent
    data class ShowToast(val message: String) : FollowUpFormEvent
}