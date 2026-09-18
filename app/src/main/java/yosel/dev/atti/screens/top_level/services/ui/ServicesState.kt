package yosel.dev.atti.screens.top_level.services.ui

import yosel.dev.atti.core.models.filter.ServiceFilter
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

data class ServicesState(
    val isLoading: Boolean = true,
    val services: List<ServiceWithDetailsModel> = emptyList(),
    val filteredServices: List<ServiceWithDetailsModel> = emptyList(),
    val searchQuery: String = "",
    val filter: ServiceFilter = ServiceFilter(),
    val showFilterSheet: Boolean = false,
    val availableCategories: List<AppCatalogModel> = emptyList()
)