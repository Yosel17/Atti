package yosel.dev.atti.screens.navigation_bar.inventory.ui

import yosel.dev.atti.core.models.filter.ProductFilter
import yosel.dev.atti.core.models.filter.ServiceFilter
import yosel.dev.atti.core.models.filter.SupplierFilter
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.SupplierModel

data class InventoryState(
    val isError: Boolean = false,
    val errorMessage: String = "",
    // Productos
    val isLoadingProducts: Boolean = true,
    val isFirstProducts: Boolean = true,
    val products: List<ProductWithDetailsModel> = emptyList(),
    val filteredProducts: List<ProductWithDetailsModel> = emptyList(),
    val productSearchQuery: String = "",
    val productFilter: ProductFilter = ProductFilter(),
    val showProductFilterSheet: Boolean = false,
    val productCategories: List<AppCatalogModel> = emptyList(),
    val productUnitTypes: List<AppCatalogModel> = emptyList(),
    val productSuppliers: List<SupplierModel> = emptyList(),
    // Servicios
    val isLoadingServices: Boolean = true,
    val isFirstServices: Boolean = true,
    val services: List<ServiceWithDetailsModel> = emptyList(),
    val filteredServices: List<ServiceWithDetailsModel> = emptyList(),
    val serviceSearchQuery: String = "",
    val serviceFilter: ServiceFilter = ServiceFilter(),
    val showServiceFilterSheet: Boolean = false,
    val serviceCategories: List<AppCatalogModel> = emptyList(),
    // Proveedores
    val isLoadingSuppliers: Boolean = true,
    val isFirstSuppliers: Boolean = true,
    val suppliers: List<SupplierModel> = emptyList(),
    val filteredSuppliers: List<SupplierModel> = emptyList(),
    val supplierSearchQuery: String = "",
    val supplierFilter: SupplierFilter = SupplierFilter(),
    val showSupplierFilterSheet: Boolean = false,
    val selectedTabIndex: Int = 0
)
