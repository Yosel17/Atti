package yosel.dev.atti.screens.detail_product.ui

import yosel.dev.atti.core.navigation.main.Screens

sealed interface DetailProductAction {
    data object OnEditClick : DetailProductAction
    data class ToggleShowDialogConfirmDelete(val show: Boolean) : DetailProductAction
    data object DeleteProduct : DetailProductAction
    data class ToggleShowDialogConfirmRestore(val show: Boolean) : DetailProductAction
    data object RestoreProduct : DetailProductAction
    data class ToggleShowDialogInformation(val show: Boolean) : DetailProductAction
    data class OnNavigationMain(val screen: Screens) : DetailProductAction
}