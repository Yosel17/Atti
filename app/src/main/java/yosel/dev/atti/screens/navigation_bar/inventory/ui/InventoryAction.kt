package yosel.dev.atti.screens.navigation_bar.inventory.ui

sealed interface InventoryAction {
    data class OnTabSelected(val index: Int) : InventoryAction
    data class OnProductSearchQueryChange(val query: String) : InventoryAction
    data class OnServiceSearchQueryChange(val query: String) : InventoryAction
    data class OnSupplierSearchQueryChange(val query: String) : InventoryAction
}