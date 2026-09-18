package yosel.dev.atti.screens.top_level.services.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.service.ServiceDao
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyDao
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.supabase.ServicesDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.top_level.services.domain.ServicesRepository
import javax.inject.Inject

class ServicesRepositoryImpl @Inject constructor(
    private val serviceDao: ServiceDao,
    private val serviceSupplyDao: ServiceSupplyDao,
    private val appCatalogDao: AppCatalogDao,
    private val supplierDao: SupplierDao,
    private val productDao: ProductDao,
    private val servicesDataSource: ServicesDataSource
) : ServicesRepository {

    override fun getAllServices(): Flow<List<ServiceWithDetailsModel>> =
        serviceDao.getAllServicesWithCatalogFlow()
            .map { entities -> entities.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    override suspend fun syncServices(): Result<Unit> = runCatching {
        val remoteServices = servicesDataSource.getAllServicesWithDetails()

        val serviceCatalogEntities = remoteServices.mapNotNull { it.category?.toEntity() }
        val productCatalogEntities = remoteServices.flatMap { service ->
            service.supplies.flatMap { supply ->
                listOfNotNull(
                    supply.product?.category?.toEntity(),
                    supply.product?.unitType?.toEntity()
                )
            }
        }
        val allCatalogs = (serviceCatalogEntities + productCatalogEntities).distinctBy { it.id }

        val supplierEntities = remoteServices.flatMap { service ->
            service.supplies.mapNotNull { it.product?.supplier?.toEntity() }
        }.distinctBy { it.id }

        val productEntities = remoteServices.flatMap { service ->
            service.supplies.mapNotNull { it.product?.toEntity() }
        }.distinctBy { it.id }

        val serviceEntities = remoteServices.map { it.toEntity() }
        val serviceSupplyEntities = remoteServices.flatMap { service ->
            service.supplies.map { it.toEntity() }
        }.distinctBy { it.id }

        if (allCatalogs.isNotEmpty()) appCatalogDao.insertAllCatalogs(allCatalogs)
        if (supplierEntities.isNotEmpty()) supplierDao.upsertSuppliers(supplierEntities)
        if (productEntities.isNotEmpty()) productDao.upsertProducts(productEntities)
        serviceDao.upsertServices(serviceEntities)
        if (serviceSupplyEntities.isNotEmpty()) serviceSupplyDao.upsertSupplies(serviceSupplyEntities)
    }
}