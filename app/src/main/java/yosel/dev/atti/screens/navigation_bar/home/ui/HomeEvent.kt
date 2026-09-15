package yosel.dev.atti.screens.navigation_bar.home.ui

sealed interface HomeEvent {
    data class ShowSnackBarError(val message: String) : HomeEvent
}