package yosel.dev.atti.screens.detail_product.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.supabase.ProductsDataSource
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.detail_product.domain.DetailProductRepository
import javax.inject.Inject

class DetailProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val productsDataSource: ProductsDataSource
): DetailProductRepository {

    override fun getProductWithDetailsByIdFlow(productId: String): Flow<ProductWithDetailsModel?> =
        productDao.getProductWithDetailsByIdFlow(productId = productId)
            .map { entity -> entity?.toModel() }
            .flowOn(Dispatchers.IO)

    override suspend fun changeStatusProduct(
        productId: String,
        newStatus: Int
    ): Result<Unit> = runCatching {
        productsDataSource.updateProductStatus(productId = productId, newStatus = newStatus)
        productDao.updateProductStatus(productId = productId, newStatus = newStatus)
    }
}