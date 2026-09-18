package yosel.dev.atti.screens.top_level.suppliers.ui

sealed interface SuppliersEvent {
    data class ShowSnackBarError(val message: String) : SuppliersEvent
    data class NavigateToPhone(val phoneNumber: String) : SuppliersEvent
    data class NavigateToWhatsapp(val phoneNumber: String) : SuppliersEvent
}