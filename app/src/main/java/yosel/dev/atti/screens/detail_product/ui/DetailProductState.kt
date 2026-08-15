package yosel.dev.atti.screens.detail_product.ui

import yosel.dev.atti.core.models.model.ProductWithDetailsModel

data class DetailProductState(
    val isLoading: Boolean = true,
    val productWithDetails: ProductWithDetailsModel = ProductWithDetailsModel(),
    val showDialogConfirmDelete: Boolean = false,
    val isLoadingDeleteProduct: Boolean = false,
    val showDialogConfirmRestore: Boolean = false,
    val isLoadingRestoreProduct: Boolean = false,
    val showDialogInformation: Boolean = false
)
