package yosel.dev.atti.screens.top_level.products.ui

import yosel.dev.atti.core.models.filter.ProductFilter
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.SupplierModel

data class ProductsState(
    val isLoading: Boolean = true,
    val products: List<ProductWithDetailsModel> = emptyList(),
    val filteredProducts: List<ProductWithDetailsModel> = emptyList(),
    val searchQuery: String = "",
    val filter: ProductFilter = ProductFilter(),
    val showFilterSheet: Boolean = false,
    val availableCategories: List<AppCatalogModel> = emptyList(),
    val availableUnitTypes: List<AppCatalogModel> = emptyList(),
    val availableSuppliers: List<SupplierModel> = emptyList()
)