package yosel.dev.atti.screens.detail_product.ui

import yosel.dev.atti.core.navigation.main.Screens

sealed interface DetailProductEvent {
    data class ShowErrorSnackbar(val message: String) : DetailProductEvent
    data class ShowSuccessSnackbar(val message: String) : DetailProductEvent
    data class OnNavigationMain(val screen: Screens) : DetailProductEvent
}