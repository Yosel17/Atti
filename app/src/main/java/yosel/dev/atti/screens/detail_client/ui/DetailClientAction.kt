package yosel.dev.atti.screens.detail_client.ui

sealed interface DetailClientAction {
    data class OnCallClick(val phoneNumber: String) : DetailClientAction
    data class OnWhatsappClick(val phoneNumber: String) : DetailClientAction
}