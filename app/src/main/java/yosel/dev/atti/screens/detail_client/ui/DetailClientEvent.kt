package yosel.dev.atti.screens.detail_client.ui

sealed interface DetailClientEvent {

    data class ShowErrorSnackbar(val message: String) : DetailClientEvent

    data class OnCallClick(val phoneNumber: String) : DetailClientEvent

    data class OnWhatsappClick(val phoneNumber: String) : DetailClientEvent
}