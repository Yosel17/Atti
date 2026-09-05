package yosel.dev.atti.screens.treatment_form.data

import androidx.room.withTransaction
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.TreatmentModel
import yosel.dev.atti.core.models.model.TreatmentWithDetailsModel
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.service.ServiceDao
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyDao
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.room.tables.treatment.TreatmentDao
import yosel.dev.atti.core.supabase.ProductsDataSource
import yosel.dev.atti.core.supabase.ServicesDataSource
import yosel.dev.atti.core.supabase.TreatmentsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.core.utils.toWithDetailsModel
import yosel.dev.atti.screens.treatment_form.domain.TreatmentFormRepository
import javax.inject.Inject

class TreatmentFormRepositoryImpl @Inject constructor(
    private val productsDataSource: ProductsDataSource,
    private val productDao: ProductDao,
    private val servicesDataSource: ServicesDataSource,
    private val serviceDao: ServiceDao,
    private val serviceSupplyDao: ServiceSupplyDao,
    private val treatmentsDataSource: TreatmentsDataSource,
    private val treatmentDao: TreatmentDao,
    private val consultationDao: ConsultationDao,
    private val consultationStepProgressDao: ConsultationStepProgressDao,
    private val appCatalogDao: AppCatalogDao,
    private val appDatabase: AppDatabase,
    private val supplierDao: SupplierDao,
) : TreatmentFormRepository {

    override suspend fun getActiveProductsWithDetails(): Result<List<ProductWithDetailsModel>> = runCatching {
        val remoteProducts = productsDataSource.getActiveProductsWithDetails()
        val appCatalogsEntities = remoteProducts.flatMap { product ->
            listOfNotNull(
                product.category?.toEntity(),
                product.unitType?.toEntity()
            )
        }.distinctBy { it.id }
        val supplierEntities = remoteProducts.mapNotNull { it.supplier?.toEntity() }.distinctBy { it.id }
        val productEntities = remoteProducts.map { it.toEntity() }

        appCatalogDao.insertAllCatalogs(appCatalogsEntities)
        supplierDao.upsertSuppliers(supplierEntities)
        productDao.upsertProducts(productEntities)

        productDao.getActiveProductsWithDetails().map { it.toModel() }
    }

    override suspend fun getActiveServicesWithDetails(): Result<List<ServiceWithDetailsModel>> = runCatching {
        val remoteServices = servicesDataSource.getActiveServicesWithDetails()

        val appCatalogsEntities = remoteServices.mapNotNull { it.category?.toEntity() }.distinctBy { it.id }
        val serviceEntities = remoteServices.map { it.toEntity() }
        val suppliesEntities = remoteServices.flatMap { it.supplies }.map { it.toEntity() }

        appCatalogDao.insertAllCatalogs(appCatalogsEntities)
        appDatabase.withTransaction {
            serviceDao.upsertServices(serviceEntities)
            if (suppliesEntities.isNotEmpty()) {
                serviceSupplyDao.upsertSupplies(suppliesEntities)
            }
        }
        serviceDao.getActiveServicesWithDetails().map { it.toModel() }
    }

    override suspend fun saveTreatments(
        consultationId: String,
        treatments: List<TreatmentModel>
    ): Result<List<TreatmentWithDetailsModel>> = runCatching {
        val treatmentsDtos = treatments.map { it.toDtoForInsert() }
        val insertedDtos = treatmentsDataSource.insertTreatments(treatmentsDtos)

        val entities = insertedDtos.map { it.toEntity() }
        treatmentDao.syncTreatmentsForConsultation(
            consultationId = consultationId,
            treatments = entities
        )

        // Marcar paso como completado
        consultationStepProgressDao.upsertSingleProgress(
            ConsultationStepProgressEntity(
                consultationId = consultationId,
                stepCatalogId = Constants.TREATMENT_STEP_DIAGNOSIS,
                recordId = insertedDtos.firstOrNull()?.id,
                isCompleted = true,
                status = Constants.ACTIVE_STATUS
            )
        )
        insertedDtos.map { it.toWithDetailsModel() }
    }

    override suspend fun updateTreatments(
        consultationId: String,
        treatments: List<TreatmentModel>
    ): Result<List<TreatmentWithDetailsModel>> = runCatching {

        val treatmentsDtos = treatments.map { it.toDtoForInsert() }

        // 1. Ejecución atómica en Supabase vía RPC (si algo falla, PostgreSQL hace rollback automático)
        val insertedDtos = treatmentsDataSource.replaceTreatmentsRpc(
            consultationId = consultationId,
            treatments = treatmentsDtos
        )

        // 2. Transacción atómica en Room para mantener sincronizada la BD local
        appDatabase.withTransaction {
            val entities = insertedDtos.map { it.toEntity() }
            treatmentDao.syncTreatmentsForConsultation(
                consultationId = consultationId,
                treatments = entities
            )
        }

        insertedDtos.map { it.toWithDetailsModel() }
    }

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(consultationId)
            ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getTreatmentsByConsultationId(consultationId: String): Result<List<TreatmentWithDetailsModel>> = runCatching {
        val localTreatments = treatmentDao.getTreatmentsWithDetailsByConsultationId(consultationId)
        if (localTreatments.isNotEmpty()) {
            return@runCatching localTreatments.map { it.toModel() }
        }
        val remoteDtos = treatmentsDataSource.getTreatmentsWithDetailsByConsultationId(consultationId)
        val entities = remoteDtos.map { it.toEntity() }
        treatmentDao.syncTreatmentsForConsultation(consultationId, entities)
        treatmentDao.getTreatmentsWithDetailsByConsultationId(consultationId).map { it.toModel() }
    }
}