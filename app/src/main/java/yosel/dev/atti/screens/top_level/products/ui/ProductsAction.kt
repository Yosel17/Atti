package yosel.dev.atti.screens.top_level.products.ui

import yosel.dev.atti.core.models.filter.ProductFilter

sealed interface ProductsAction {
    data class OnSearchQueryChange(val query: String) : ProductsAction
    data class OnApplyFilter(val filter: ProductFilter) : ProductsAction
    data class OnToggleFilterSheet(val isOpen: Boolean) : ProductsAction
}