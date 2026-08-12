package yosel.dev.atti.screens.add_supplier.ui

sealed interface AddSupplierEvent {
    data class ShowErrorSnackbar(val message: String) : AddSupplierEvent
    data class ShowSuccessSnackbar(val message: String) : AddSupplierEvent
}