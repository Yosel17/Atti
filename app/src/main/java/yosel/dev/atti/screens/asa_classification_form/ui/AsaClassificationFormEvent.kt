package yosel.dev.atti.screens.asa_classification_form.ui

sealed interface AsaClassificationFormEvent {
    data class ShowErrorSnackbar(val message: String) : AsaClassificationFormEvent
    data class ShowSuccessSnackbar(val message: String) : AsaClassificationFormEvent
    data class ShowToast(val message: String) : AsaClassificationFormEvent
}
