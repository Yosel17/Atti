package yosel.dev.atti.screens.detail_supplier.ui

sealed interface DetailSupplierEvent {
    data class ShowErrorSnackbar(val message: String) : DetailSupplierEvent
    data class ShowSuccessSnackbar(val message: String) : DetailSupplierEvent
    data class OnCallClick(val phoneNumber: String) : DetailSupplierEvent
    data class OnWhatsappClick(val phoneNumber: String) : DetailSupplierEvent
}