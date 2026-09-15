package yosel.dev.atti.screens.navigation_bar.inventory.ui

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
}