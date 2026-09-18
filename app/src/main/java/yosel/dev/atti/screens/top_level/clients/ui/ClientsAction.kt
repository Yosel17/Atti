package yosel.dev.atti.screens.top_level.clients.ui

import yosel.dev.atti.core.models.filter.ClientFilter

sealed interface ClientsAction {
    data class OnSearchQueryChange(val query: String) : ClientsAction
    data class OnApplyFilter(val filter: ClientFilter) : ClientsAction
    data class OnToggleFilterSheet(val isOpen: Boolean) : ClientsAction
    data class OnCallClick(val phoneNumber: String) : ClientsAction
    data class OnWhatsappClick(val phoneNumber: String) : ClientsAction
}