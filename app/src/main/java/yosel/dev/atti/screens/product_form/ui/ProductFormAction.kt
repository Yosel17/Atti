package yosel.dev.atti.screens.product_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.SupplierModel

sealed interface ProductFormAction {

    data object RegisterProduct : ProductFormAction
    data object TryCatalogsAgain : ProductFormAction
    data class OnChangeValueFormInputState(val value: String, val field: Int) : ProductFormAction
    data object OnOpenCategorySheet : ProductFormAction
    data class OnSearchCategoryQueryChange(val query: String) : ProductFormAction
    data class OnSelectCategory(val category: AppCatalogModel) : ProductFormAction
    data object OnDismissCategorySheet: ProductFormAction
    data class OnShowAddCatalogDialog(val catalogTypeId: Int, val catalogTypeName: String) : ProductFormAction
    data object OnDismissAddAppCatalogDialog: ProductFormAction
    data class OnSaveAppCatalog(val name: String) : ProductFormAction
    data object OnOpenUnitsMeasurementSheet: ProductFormAction
    data object OnDismissUnitsMeasurementSheet: ProductFormAction
    data class OnSearchUnitsMeasurementQueryChange(val query: String) : ProductFormAction
    data class OnSelectUnitsMeasurement(val unitsOfMeasurement: AppCatalogModel) : ProductFormAction
    data object OnOpenSupplierSheet: ProductFormAction
    data object OnDismissSupplierSheet: ProductFormAction
    data class OnSearchSupplierQueryChange(val query: String) : ProductFormAction
    data class OnSelectSupplier(val supplier: SupplierModel) : ProductFormAction
}