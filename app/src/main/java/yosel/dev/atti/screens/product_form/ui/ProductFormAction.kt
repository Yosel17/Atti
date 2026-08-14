package yosel.dev.atti.screens.product_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClientModel

sealed interface ProductFormAction {

    data object RegisterProduct : ProductFormAction

    data object TryCatalogsAgain : ProductFormAction

    data class OnChangeValueFormInputState(val value: String, val field: Int) : ProductFormAction

    data object OnOpenClientSheet : ProductFormAction

    data class OnSearchCategoryQueryChange(val query: String) : ProductFormAction

    data class OnSelectCategory(val category: AppCatalogModel) : ProductFormAction

    data object OnDismissCategorySheet: ProductFormAction
}