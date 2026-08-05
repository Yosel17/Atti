package yosel.dev.atti.screens.add_client.ui

data class AddClientState(
    val isLoadingAddClient: Boolean = false,
    val formState: AddClientFormState = AddClientFormState(),
)
