package yosel.dev.atti.screens.service_form.data

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceModel
import yosel.dev.atti.core.models.model.ServiceSupplyModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.service.ServiceDao
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyDao
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.ProductsDataSource
import yosel.dev.atti.core.supabase.ServiceSuppliesDataSource
import yosel.dev.atti.core.supabase.ServicesDataSource
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.service_form.domain.ServiceFormRepository
import javax.inject.Inject

class ServiceFormRepositoryImpl @Inject constructor(
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val servicesDataSource: ServicesDataSource,
    private val serviceDao: ServiceDao,
    private val serviceSuppliesDataSource: ServiceSuppliesDataSource,
    private val serviceSuppliesDao: ServiceSupplyDao,
    private val productsDataSource: ProductsDataSource,
    private val productDao: ProductDao
): ServiceFormRepository {

    override suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>> = runCatching {
        val remoteAppCatalogs = appCatalogsDataSource.getCatalogsByTypes(types = types)
        val entities = remoteAppCatalogs.map { it.toEntity() }
        appCatalogDao.insertAllCatalogs(catalogs = entities)
        remoteAppCatalogs.map { it.toModel() }
    }

    override suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel> =runCatching {
        val appCatalogDto = appCatalogsDataSource.insertAndGetCatalog(catalog = catalog.toDtoForInsert())
        appCatalogDao.insertCatalog(catalog = appCatalogDto.toEntity())
        appCatalogDto.toModel()
    }

    override suspend fun insertService(service: ServiceModel): Result<Unit> = runCatching {
        val serviceDto = servicesDataSource.insertServiceAndReturn(service = service.toDtoForInsert())
        serviceDao.upsertService(service = serviceDto.toEntity())
    }

    override suspend fun insertServiceSupplies(supplies: List<ServiceSupplyModel>): Result<Unit> = runCatching {
        val serviceSuppliesDto = supplies.map { it.toDtoForInsert() }
        val responseServiceSuppliesDto = serviceSuppliesDataSource.insertAndGetSupplies(supplies = serviceSuppliesDto)
        val serviceSuppliesEntities = responseServiceSuppliesDto.map { it.toEntity() }
        serviceSuppliesDao.upsertSupplies(supplies = serviceSuppliesEntities)
    }

    override suspend fun getActiveProductsWithDetails(): Result<List<ProductWithDetailsModel>> = runCatching {
        val productsWithDetailsDto = productsDataSource.getActiveProductsWithDetails()
        val appCatalogsEntities = productsWithDetailsDto.flatMap { product ->
            listOfNotNull(
                product.category?.toEntity(),
                product.unitType?.toEntity()
            )
        }.distinctBy { it.id }
        val productEntities = productsWithDetailsDto.map { it.toEntity() }

        appCatalogDao.insertAllCatalogs(appCatalogsEntities)
        productDao.upsertProducts(products = productEntities)

        productDao.getActiveProductsWithDetails().map { it.toModel() }
    }
}