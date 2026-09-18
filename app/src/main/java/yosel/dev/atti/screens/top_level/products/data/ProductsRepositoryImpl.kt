package yosel.dev.atti.screens.top_level.products.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.supabase.ProductsDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.top_level.products.domain.ProductsRepository
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val supplierDao: SupplierDao,
    private val appCatalogDao: AppCatalogDao,
    private val productsDataSource: ProductsDataSource
) : ProductsRepository {

    override fun getAllProducts(): Flow<List<ProductWithDetailsModel>> =
        productDao.getAllProductsWithDetailsFlow()
            .map { entities -> entities.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    override suspend fun syncProducts(): Result<Unit> = runCatching {
        val remoteProducts = productsDataSource.getAllProductsWithDetails()
        val appCatalogsEntities = remoteProducts.flatMap { product ->
            listOfNotNull(
                product.category?.toEntity(),
                product.unitType?.toEntity()
            )
        }.distinctBy { it.id }
        val supplierEntities = remoteProducts.mapNotNull { it.supplier?.toEntity() }
        val productEntities = remoteProducts.map { it.toEntity() }

        if (appCatalogsEntities.isNotEmpty()) appCatalogDao.insertAllCatalogs(appCatalogsEntities)
        if (supplierEntities.isNotEmpty()) supplierDao.upsertSuppliers(supplierEntities)
        productDao.upsertProducts(productEntities)
    }
}