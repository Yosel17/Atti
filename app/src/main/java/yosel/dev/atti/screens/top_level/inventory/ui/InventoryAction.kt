package yosel.dev.atti.screens.top_level.inventory.ui

import yosel.dev.atti.core.models.filter.ProductFilter
import yosel.dev.atti.core.models.filter.ServiceFilter
import yosel.dev.atti.core.models.filter.SupplierFilter

sealed interface InventoryAction {
    data class OnTabSelected(val index: Int) : InventoryAction
    data class OnProductSearchQueryChange(val query: String) : InventoryAction
    data class OnServiceSearchQueryChange(val query: String) : InventoryAction
    data class OnSupplierSearchQueryChange(val query: String) : InventoryAction
    data class OnCallClick(val phoneNumber: String): InventoryAction
    data class OnWhatsappClick(val phoneNumber: String): InventoryAction
    data class OnApplyProductFilter(val filter: ProductFilter) : InventoryAction
    data class OnApplyServiceFilter(val filter: ServiceFilter) : InventoryAction
    data class OnApplySupplierFilter(val filter: SupplierFilter) : InventoryAction
    // Control de visibilidad de sheets
    data class OnToggleProductFilterSheet(val isOpen: Boolean) : InventoryAction
    data class OnToggleServiceFilterSheet(val isOpen: Boolean) : InventoryAction
    data class OnToggleSupplierFilterSheet(val isOpen: Boolean) : InventoryAction
}