package yosel.dev.atti.screens.product_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.SupplierModel

data class ProductFormState(
    val isLoadingDataInitial: Boolean = true,
    val productCategoryCatalog: List<AppCatalogModel> = emptyList(),
    val productUnitOfMeasureCatalog: List<AppCatalogModel> = emptyList(),
    val isSuccessGetCategory: Boolean = false,
    val suppliers: List<SupplierModel> = emptyList(),
    val filteredSuppliers: List<SupplierModel> = emptyList(),
    val isSuccessGetSuppliers: Boolean = false,
    val formInputState: ProductFormInputsState = ProductFormInputsState()
)
