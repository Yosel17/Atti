package yosel.dev.atti.screens.add_client.ui

sealed interface AddClientAction {

    data class OnChangeValueFormState(val value: String, val field: Int): AddClientAction
}