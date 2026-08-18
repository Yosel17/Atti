package yosel.dev.atti.screens.service_form.data

import androidx.room.withTransaction
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceModel
import yosel.dev.atti.core.models.model.ServiceSupplyModel
import yosel.dev.atti.core.models.request.CreateServiceRequest
import yosel.dev.atti.core.room.config.AppDatabase
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
    private val serviceSuppliesDao: ServiceSupplyDao,
    private val productsDataSource: ProductsDataSource,
    private val productDao: ProductDao,
    private val appDatabase: AppDatabase,
): ServiceFormRepository {
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

    override suspend fun insertServiceWithSupplies(
        service: ServiceModel,
        supplies: List<ServiceSupplyModel>
    ): Result<ServiceModel> = runCatching {
        val request = CreateServiceRequest(
            service_data = service.toDtoForInsert(),
            supplies_data = supplies.map { it.toDtoForInsert() }
        )

        // 1. Supabase realiza la transacción atómica
        val responseServiceDto = servicesDataSource.insertServiceWithSupplies(request)

        val savedServiceEntity = responseServiceDto.toEntity()
        // Obtenemos los insumos con los IDs reales creados por Postgres
        val suppliesEntities = responseServiceDto.supplies.map { it.toEntity() }

        // 2. Room guarda de forma transaccional
        appDatabase.withTransaction {
            serviceDao.upsertService(savedServiceEntity)
            if (suppliesEntities.isNotEmpty()) {
                serviceSuppliesDao.upsertSupplies(suppliesEntities)
            }
        }

        responseServiceDto.toModel()
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