package yosel.dev.atti.screens.clients.ui

sealed interface DirectoryEvent {
    data class ShowSnackBarError(val message: String): DirectoryEvent
    data class NavigateToPhone(val phoneNumber: String): DirectoryEvent
    data class NavigateToWhatsapp(val phoneNumber: String): DirectoryEvent
}