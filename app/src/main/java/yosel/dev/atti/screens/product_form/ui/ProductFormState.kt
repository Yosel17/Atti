package yosel.dev.atti.screens.product_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

data class ProductFormState(
    val isLoadingDataInitial: Boolean = true,
    val productCategoryCatalog: List<AppCatalogModel> = emptyList(),
    val productUnitOfMeasureCatalog: List<AppCatalogModel> = emptyList(),
    val isSuccessGetCategory: Boolean = false
)
