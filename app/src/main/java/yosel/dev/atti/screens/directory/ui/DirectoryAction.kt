package yosel.dev.atti.screens.directory.ui

sealed interface DirectoryAction {

    data class OnTabSelected(val index: Int): DirectoryAction
    data class OnCallClick(val phoneNumber: String): DirectoryAction
    data class OnWhatsappClick(val phoneNumber: String): DirectoryAction
    data class OnClientSearchQueryChange(val query: String): DirectoryAction
    data class OnPatientSearchQueryChange(val query: String): DirectoryAction
}