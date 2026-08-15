package yosel.dev.atti.screens.detail_product.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ProductWithDetailsModel

interface DetailProductRepository {

    fun getProductWithDetailsByIdFlow(productId: String): Flow<ProductWithDetailsModel?>

    suspend fun changeStatusProduct(productId: String, newStatus: Int): Result<Unit>
}