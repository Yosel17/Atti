package yosel.dev.atti.screens.navigation_bar.home.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.FollowUpWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.follow_up.FollowUpDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.supabase.FollowUpsDataSource
import yosel.dev.atti.core.supabase.ProductsDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.navigation_bar.home.domain.HomeRepository
import java.time.YearMonth
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val followUpsDataSource: FollowUpsDataSource,
    private val followUpDao: FollowUpDao,
    private val productsDataSource: ProductsDataSource,
    private val productDao: ProductDao,
    private val appCatalogDao: AppCatalogDao,
    private val patientDao: PatientDao,
    private val clientDao: ClientDao,
    private val consultationDao: ConsultationDao,
    private val supplierDao: SupplierDao
) : HomeRepository {

    override fun getFollowUpsForMonthFlow(yearMonth: YearMonth): Flow<List<FollowUpWithDetailsModel>> {
        val startDateIso = "${yearMonth.atDay(1)}T00:00:00"
        val endDateIso = "${yearMonth.atEndOfMonth()}T23:59:59"
        return followUpDao.getFollowUpsWithDetailsByDateRangeFlow(startDateIso, endDateIso)
            .map { entities -> entities.map { it.toModel() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncFollowUpsForMonth(yearMonth: YearMonth): Result<Unit> = runCatching {
        val startDateIso = "${yearMonth.atDay(1)}T00:00:00Z"
        val endDateIso = "${yearMonth.atEndOfMonth()}T23:59:59Z"
        val remoteFollowUps = followUpsDataSource.getFollowUpsByDateRange(startDateIso, endDateIso)

        // 1. Extraer y persistir catálogos hijos (especie, género, tipo de consulta)
        val catalogEntities = remoteFollowUps.flatMap { dto ->
            listOfNotNull(
                dto.patient?.species?.toEntity(),
                dto.patient?.gender?.toEntity(),
                dto.consultation?.consultationType?.toEntity(),
                dto.consultation?.patient?.species?.toEntity(),
                dto.consultation?.patient?.gender?.toEntity()
            )
        }.distinctBy { it.id }

        // 2. Extraer clientes y pacientes para no violar Foreign Keys
        val clientEntities = remoteFollowUps.mapNotNull { it.patient?.client?.toEntity() }.distinctBy { it.id }
        val patientEntities = remoteFollowUps.mapNotNull { it.patient?.toEntity() }.distinctBy { it.id }
        val consultationEntities = remoteFollowUps.mapNotNull { it.consultation?.toEntity() }.distinctBy { it.id }
        val followUpEntities = remoteFollowUps.map { it.toEntity() }

        if (catalogEntities.isNotEmpty()) appCatalogDao.insertAllCatalogs(catalogEntities)
        if (clientEntities.isNotEmpty()) clientDao.upsertClients(clientEntities)
        if (patientEntities.isNotEmpty()) patientDao.upsertPatients(patientEntities)
        if (consultationEntities.isNotEmpty()) consultationDao.upsertConsultations(consultationEntities)
        if (followUpEntities.isNotEmpty()) followUpDao.upsertFollowUps(followUpEntities)
    }

    override fun getLowStockProductsFlow(): Flow<List<ProductWithDetailsModel>> =
        productDao.getLowStockProductsWithDetailsFlow()
            .map { entities -> entities.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    override suspend fun syncLowStockProducts(): Result<Unit> = runCatching {
        val remoteProducts = productsDataSource.getLowStockProductsWithDetails()

        val catalogEntities = remoteProducts.flatMap { product ->
            listOfNotNull(
                product.category?.toEntity(),
                product.unitType?.toEntity()
            )
        }.distinctBy { it.id }

        val supplierEntities = remoteProducts.mapNotNull { it.supplier?.toEntity() }.distinctBy { it.id }
        val productEntities = remoteProducts.map { it.toEntity() }

        if (catalogEntities.isNotEmpty()) appCatalogDao.insertAllCatalogs(catalogEntities)
        if (supplierEntities.isNotEmpty()) supplierDao.upsertSuppliers(supplierEntities)
        if (productEntities.isNotEmpty()) productDao.upsertProducts(productEntities)
    }
}