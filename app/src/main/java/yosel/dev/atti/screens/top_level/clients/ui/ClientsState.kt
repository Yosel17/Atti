package yosel.dev.atti.screens.top_level.clients.ui

import yosel.dev.atti.core.models.filter.ClientFilter
import yosel.dev.atti.core.models.model.ClientModel

data class ClientsState(
    val isLoading: Boolean = true,
    val clients: List<ClientModel> = emptyList(),
    val filteredClients: List<ClientModel> = emptyList(),
    val searchQuery: String = "",
    val filter: ClientFilter = ClientFilter(),
    val showFilterSheet: Boolean = false
)
