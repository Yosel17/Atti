package yosel.dev.atti.screens.clients.ui

sealed interface ClientsEvent {
    data class ShowSnackBarError(val message: String): ClientsEvent
}