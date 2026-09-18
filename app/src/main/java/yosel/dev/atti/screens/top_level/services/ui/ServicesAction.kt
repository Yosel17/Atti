package yosel.dev.atti.screens.top_level.services.ui

import yosel.dev.atti.core.models.filter.ServiceFilter

sealed interface ServicesAction {
    data class OnSearchQueryChange(val query: String) : ServicesAction
    data class OnApplyFilter(val filter: ServiceFilter) : ServicesAction
    data class OnToggleFilterSheet(val isOpen: Boolean) : ServicesAction
}