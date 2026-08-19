package yosel.dev.atti.screens.service_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceModel

data class ServiceFormState(
    val isEditMode: Boolean = false,
    val serviceId: String? = null,
    val currentService: ServiceModel? = null,
    val initialFormInputState: ServiceFormInputsState = ServiceFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val categories: List<AppCatalogModel> = emptyList(),
    val filteredCategories: List<AppCatalogModel> = emptyList(),
    val isSuccessGetCategory: Boolean = false,
    val formInputState: ServiceFormInputsState = ServiceFormInputsState(),
    val isCategorySheetOpen: Boolean = false,
    val categorySearchQuery: String = "",
    val showAddAppCatalogDialog: Boolean = false,
    val activeCatalogTypeId: Int = 0,
    val activeCatalogTypeName: String = "",
    val isLoadingAddCatalog: Boolean = false,
    // Estado para selector de insumos/productos
    val isLoadingProducts: Boolean = false,
    val productsWithDetails: List<ProductWithDetailsModel> = emptyList(),
    val filteredProductsWithDetails: List<ProductWithDetailsModel> = emptyList(),
    val isProductSheetOpen: Boolean = false,
    val productSearchQuery: String = "",
    val tempSelectedProductIds: Set<String> = emptySet(),
    val isLoadingRegisterService: Boolean = false,
    val isLoadingUpdateService: Boolean = false
)
