package yosel.dev.atti.screens.top_level.products.ui

sealed interface ProductsEvent {
    data class ShowSnackBarError(val message: String) : ProductsEvent
}