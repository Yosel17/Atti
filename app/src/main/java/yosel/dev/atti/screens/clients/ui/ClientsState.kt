package yosel.dev.atti.screens.clients.ui

import yosel.dev.atti.core.models.model.ClientModel

data class ClientsState(
    val isError: Boolean = false,
    val errorMessage: String = "",
    val isLoading: Boolean = true,
    val clients: List<ClientModel> = emptyList()
)
