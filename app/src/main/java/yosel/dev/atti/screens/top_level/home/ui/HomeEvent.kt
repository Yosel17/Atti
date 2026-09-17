package yosel.dev.atti.screens.top_level.home.ui

sealed interface HomeEvent {
    data class ShowSnackBarError(val message: String) : HomeEvent
}