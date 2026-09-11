package yosel.dev.atti.screens.pre_anesthetic_test_form.ui

sealed interface PreAnestheticTestFormEvent {
    data class ShowErrorSnackbar(val message: String) : PreAnestheticTestFormEvent
    data class ShowSuccessSnackbar(val message: String) : PreAnestheticTestFormEvent
    data class ShowToast(val message: String) : PreAnestheticTestFormEvent
}
