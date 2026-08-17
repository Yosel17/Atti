package yosel.dev.atti.screens.service_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductModel

sealed interface ServiceFormAction {
    data object TryCatalogsAgain : ServiceFormAction
    data class OnChangeValueFormInputState(val value: String, val field: Int) : ServiceFormAction
    data object OnOpenCategorySheet : ServiceFormAction
    data object OnDismissCategorySheet : ServiceFormAction
    data class OnSearchCategoryQueryChange(val query: String) : ServiceFormAction
    data class OnSelectCategory(val category: AppCatalogModel) : ServiceFormAction
    data class OnShowAddCatalogDialog(val catalogTypeId: Int, val catalogTypeName: String) : ServiceFormAction
    data object OnDismissAddAppCatalogDialog : ServiceFormAction
    data class OnSaveAppCatalog(val name: String) : ServiceFormAction

    // Gastos de Insumos y Productos
    data class OnChangeExpenseMode(val mode: ExpenseMode) : ServiceFormAction
    data object OnOpenProductSheet : ServiceFormAction
    data object OnDismissProductSheet : ServiceFormAction
    data class OnSearchProductQueryChange(val query: String) : ServiceFormAction
    data class OnToggleSelectProduct(val product: ProductModel) : ServiceFormAction
    data object OnConfirmProductSelection : ServiceFormAction
    data class OnIncrementProductQuantity(val productId: String) : ServiceFormAction
    data class OnDecrementProductQuantity(val productId: String) : ServiceFormAction
    data class OnRemoveProductSupply(val productId: String) : ServiceFormAction
    data object OnSaveService : ServiceFormAction
}