package yosel.dev.atti.screens.detail_client.ui

sealed interface DetailClientEvent {

    data class ShowErrorSnackbar(val message: String) : DetailClientEvent
}