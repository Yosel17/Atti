package yosel.dev.atti.screens.service_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

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
}