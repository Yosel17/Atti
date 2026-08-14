package yosel.dev.atti.screens.product_form.ui

sealed interface ProductFormEvent {

    data class ShowErrorSnackbar(val message: String) : ProductFormEvent

    data class ShowSuccessSnackbar(val message: String) : ProductFormEvent

    data class ShowToast(val message: String) : ProductFormEvent
}