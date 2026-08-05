package yosel.dev.atti.screens.directory.ui

import yosel.dev.atti.core.models.model.ClientModel

data class DirectoryState(
    val isError: Boolean = false,
    val errorMessage: String = "",
    val isLoadingClients: Boolean = true,
    val clients: List<ClientModel> = emptyList(),
    val filteredClients: List<ClientModel> = emptyList(),
    val selectedTabIndex: Int = 0,
    val searchQuery: String = ""
)