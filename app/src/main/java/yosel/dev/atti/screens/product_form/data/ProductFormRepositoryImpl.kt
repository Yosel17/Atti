package yosel.dev.atti.screens.product_form.data

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductModel
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.ProductsDataSource
import yosel.dev.atti.core.supabase.SuppliersDataSource
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.product_form.domain.ProductFormRepository
import javax.inject.Inject

class ProductFormRepositoryImpl @Inject constructor(
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val productsDataSource: ProductsDataSource,
    private val productDao: ProductDao,
    private val suppliersDataSource: SuppliersDataSource,
    private val suppliersDao: SupplierDao
): ProductFormRepository {

    override suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>> = runCatching {
        val remoteAppCatalogs = appCatalogsDataSource.getCatalogsByTypes(types = types)
        val entities = remoteAppCatalogs.map { it.toEntity() }
        appCatalogDao.insertAllCatalogs(catalogs = entities)
        remoteAppCatalogs.map { it.toModel() }
    }

    override suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel> = runCatching {
        val appCatalogDto = appCatalogsDataSource.insertAndGetCatalog(catalog = catalog.toDtoForInsert())
        appCatalogDao.insertCatalog(catalog = appCatalogDto.toEntity())
        appCatalogDto.toModel()
    }

    override suspend fun insertProduct(product: ProductModel): Result<Unit> = runCatching {
        val productDto = productsDataSource.insertAndGetProduct(product = product.toDtoForInsert())
        productDao.upsertProduct(product = productDto.toEntity())
    }

    override suspend fun getSuppliers(): Result<List<SupplierModel>> = runCatching {
        val suppliersDto = suppliersDataSource.getSuppliersActives()
        val entities = suppliersDto.map { it.toEntity() }
        suppliersDao.upsertSuppliers(suppliers = entities)
        suppliersDto.map { it.toModel() }
    }

    override suspend fun updateProduct(product: ProductModel): Result<Unit> = runCatching {
        productsDataSource.updateProduct(product = product.toDtoForUpdate())
        productDao.upsertProduct(product = product.toEntity())
    }
}