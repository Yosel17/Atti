package yosel.dev.atti.screens.auxiliary_test_form.ui

sealed interface AuxiliaryTestFormEvent {
    data class ShowErrorSnackbar(val message: String) : AuxiliaryTestFormEvent
    data class ShowSuccessSnackbar(val message: String) : AuxiliaryTestFormEvent
    data class ShowToast(val message: String) : AuxiliaryTestFormEvent
}
