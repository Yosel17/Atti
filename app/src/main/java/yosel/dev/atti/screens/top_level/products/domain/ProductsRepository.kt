package yosel.dev.atti.screens.top_level.products.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ProductWithDetailsModel

interface ProductsRepository {
    fun getAllProducts(): Flow<List<ProductWithDetailsModel>>
    suspend fun syncProducts(): Result<Unit>
}