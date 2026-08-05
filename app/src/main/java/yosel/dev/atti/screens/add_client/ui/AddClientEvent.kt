package yosel.dev.atti.screens.add_client.ui

sealed interface AddClientEvent {

    data class ShowErrorSnackbar(val message: String) : AddClientEvent

    data class ShowSuccessSnackbar(val message: String) : AddClientEvent
}