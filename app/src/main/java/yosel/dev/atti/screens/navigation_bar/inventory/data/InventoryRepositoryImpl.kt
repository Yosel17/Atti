package yosel.dev.atti.screens.navigation_bar.inventory.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.dto.SupplierDto
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.service.ServiceDao
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.supabase.ProductsDataSource
import yosel.dev.atti.core.supabase.ServicesDataSource
import yosel.dev.atti.core.supabase.SuppliersDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.navigation_bar.inventory.domain.InventoryRepository
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val serviceDao: ServiceDao,
    private val supplierDao: SupplierDao,
    private val productsDataSource: ProductsDataSource,
    private val servicesDataSource: ServicesDataSource,
    private val suppliersDataSource: SuppliersDataSource,
    private val appCatalogDao: AppCatalogDao
) : InventoryRepository {

    override fun getAllProducts(): Flow<List<ProductWithDetailsModel>> =
        productDao.getAllProductsWithDetailsFlow()
            .map { entities ->
                entities.map { it.toModel() }
            }
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

        appCatalogDao.insertAllCatalogs(appCatalogsEntities)
        supplierDao.upsertSuppliers(supplierEntities)
        productDao.upsertProducts(productEntities)
    }


    override fun getAllServices(): Flow<List<ServiceWithDetailsModel>> =
        serviceDao.getAllServicesWithCatalogFlow()
            .map { entities ->
                entities.map { it.toModel() }
            }
            .flowOn(Dispatchers.IO)


    override suspend fun syncServices(): Result<Unit> = runCatching {
        val remoteServices = servicesDataSource.getAllServicesWithDetails()
        val appCatalogsEntities = remoteServices.mapNotNull { it.category?.toEntity() }
        val serviceEntities = remoteServices.map { it.toEntity() }

        appCatalogDao.insertAllCatalogs(appCatalogsEntities)
        serviceDao.upsertServices(serviceEntities)
    }

    override fun getAllSuppliers(): Flow<List<SupplierModel>> =
        supplierDao.getAllSuppliersFlow()
            .map { entities ->
                entities.map { it.toModel() }
            }
            .flowOn(Dispatchers.IO)


    override suspend fun syncSuppliers(): Result<Unit> = runCatching {
        val remoteSuppliers = suppliersDataSource.getAllSuppliers()
        val supplierEntities = remoteSuppliers.map { it.toEntity() }
        supplierDao.upsertSuppliers(supplierEntities)
    }
}