package yosel.dev.atti.screens.top_level.clients.ui

sealed interface ClientsEvent {
    data class ShowSnackBarError(val message: String) : ClientsEvent
    data class NavigateToPhone(val phoneNumber: String) : ClientsEvent
    data class NavigateToWhatsapp(val phoneNumber: String) : ClientsEvent
}