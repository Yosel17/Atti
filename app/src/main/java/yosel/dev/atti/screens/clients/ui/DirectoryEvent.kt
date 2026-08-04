package yosel.dev.atti.screens.clients.ui

sealed interface DirectoryEvent {
    data class ShowSnackBarError(val message: String): DirectoryEvent
}