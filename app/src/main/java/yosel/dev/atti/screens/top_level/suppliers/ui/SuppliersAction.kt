package yosel.dev.atti.screens.top_level.suppliers.ui

import yosel.dev.atti.core.models.filter.SupplierFilter

sealed interface SuppliersAction {
    data class OnSearchQueryChange(val query: String) : SuppliersAction
    data class OnApplyFilter(val filter: SupplierFilter) : SuppliersAction
    data class OnToggleFilterSheet(val isOpen: Boolean) : SuppliersAction
    data class OnCallClick(val phoneNumber: String) : SuppliersAction
    data class OnWhatsappClick(val phoneNumber: String) : SuppliersAction
}