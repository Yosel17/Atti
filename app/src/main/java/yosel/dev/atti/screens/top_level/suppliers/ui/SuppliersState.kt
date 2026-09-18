package yosel.dev.atti.screens.top_level.suppliers.ui

import yosel.dev.atti.core.models.filter.SupplierFilter
import yosel.dev.atti.core.models.model.SupplierModel

data class SuppliersState(
    val isLoading: Boolean = true,
    val suppliers: List<SupplierModel> = emptyList(),
    val filteredSuppliers: List<SupplierModel> = emptyList(),
    val searchQuery: String = "",
    val filter: SupplierFilter = SupplierFilter(),
    val showFilterSheet: Boolean = false
)