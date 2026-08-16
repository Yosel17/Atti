package yosel.dev.atti.screens.product_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductModel
import yosel.dev.atti.core.models.model.SupplierModel

data class ProductFormState(
    val isEditMode: Boolean = false,
    val productId: String? = null,
    val currentProduct: ProductModel? = null,
    val initialFormInputState: ProductFormInputsState = ProductFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val categories: List<AppCatalogModel> = emptyList(),
    val filteredCategories: List<AppCatalogModel> = emptyList(),
    val unitsOfMeasurement: List<AppCatalogModel> = emptyList(),
    val filteredUnitsOfMeasurement: List<AppCatalogModel> = emptyList(),
    val isSuccessGetCategory: Boolean = false,
    val suppliers: List<SupplierModel> = emptyList(),
    val filteredSuppliers: List<SupplierModel> = emptyList(),
    val isSuccessGetSuppliers: Boolean = false,
    val formInputState: ProductFormInputsState = ProductFormInputsState(),
    val isCategorySheetOpen: Boolean = false,
    val categorySearchQuery: String = "",
    val showAddAppCatalogDialog: Boolean = false,
    val activeCatalogTypeId: Int = 0,
    val activeCatalogTypeName: String = "",
    val isLoadingAddCatalog: Boolean = false,
    val isUnitsOfMeasurementSheetOpen: Boolean = false,
    val unitsOfMeasurementSearchQuery: String = "",
    val isSupplierSheetOpen: Boolean = false,
    val supplierSearchQuery: String = "",
    val isLoadingRegisterProduct: Boolean = false,
    val isLoadingUpdateProduct: Boolean = false
)
