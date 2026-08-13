package yosel.dev.atti.screens.product_form.ui

sealed interface ProductFormAction {

    data object RegisterProduct : ProductFormAction

    data object TryCatalogsAgain : ProductFormAction
}