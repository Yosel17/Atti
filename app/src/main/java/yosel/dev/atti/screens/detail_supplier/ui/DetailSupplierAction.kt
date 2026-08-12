package yosel.dev.atti.screens.detail_supplier.ui

sealed interface DetailSupplierAction {
    data class OnCallClick(val phoneNumber: String) : DetailSupplierAction
    data class OnWhatsappClick(val phoneNumber: String) : DetailSupplierAction
    data object OnEditClick : DetailSupplierAction
    data object OnDismissEdit : DetailSupplierAction
    data object OnUpdateSupplier : DetailSupplierAction
    data class OnChangeEditFormValue(val value: String, val field: Int) : DetailSupplierAction
    data class ToggleShowDialogConfirmDelete(val show: Boolean) : DetailSupplierAction
    data object DeleteSupplier : DetailSupplierAction
    data class ToggleShowDialogConfirmRestore(val show: Boolean) : DetailSupplierAction
    data object RestoreSupplier : DetailSupplierAction
    data class ToggleShowDialogInformation(val show: Boolean) : DetailSupplierAction
}