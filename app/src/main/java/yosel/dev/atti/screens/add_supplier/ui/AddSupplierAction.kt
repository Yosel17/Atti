package yosel.dev.atti.screens.add_supplier.ui

sealed interface AddSupplierAction {
    data class OnChangeValueFormState(val value: String, val field: Int) : AddSupplierAction
    data object AddSupplier : AddSupplierAction
}