package yosel.dev.atti.screens.clients.ui

sealed interface DirectoryAction {

    data class OnTabSelected(val index: Int): DirectoryAction
    data class OnCallClick(val phoneNumber: String): DirectoryAction
    data class OnWhatsappClick(val phoneNumber: String): DirectoryAction
}