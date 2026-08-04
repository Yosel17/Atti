package yosel.dev.atti.screens.clients.ui

sealed interface DirectoryAction {

    data class OnTabSelected(val index: Int): DirectoryAction
}