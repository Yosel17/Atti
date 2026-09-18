package yosel.dev.atti.screens.top_level.services.ui

sealed interface ServicesEvent {
    data class ShowSnackBarError(val message: String) : ServicesEvent
}