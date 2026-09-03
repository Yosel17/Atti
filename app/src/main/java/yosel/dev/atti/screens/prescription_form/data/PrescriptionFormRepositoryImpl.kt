package yosel.dev.atti.screens.prescription_form.data

import androidx.room.withTransaction
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PrescriptionItemModel
import yosel.dev.atti.core.models.model.PrescriptionModel
import yosel.dev.atti.core.models.model.PrescriptionWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.request.CreatePrescriptionRequest
import yosel.dev.atti.core.models.request.UpdatePrescriptionRequest
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.room.tables.prescription.PrescriptionDao
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.PrescriptionsDataSource
import yosel.dev.atti.core.supabase.ProductsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.core.utils.toWithDetailsModel
import yosel.dev.atti.screens.prescription_form.domain.PrescriptionFormRepository
import javax.inject.Inject

class PrescriptionFormRepositoryImpl @Inject constructor(
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val productsDataSource: ProductsDataSource,
    private val productDao: ProductDao,
    private val prescriptionsDataSource: PrescriptionsDataSource,
    private val prescriptionDao: PrescriptionDao,
    private val consultationDao: ConsultationDao,
    private val consultationStepProgressDao: ConsultationStepProgressDao,
    private val appDatabase: AppDatabase
) : PrescriptionFormRepository {

    companion object {


    }

    override suspend fun getPresetCatalogs(): Result<List<AppCatalogModel>> = runCatching {
        val remoteCatalogs = appCatalogsDataSource.getCatalogsByTypes(listOf(Constants.PRESETS_CATALOG_TYPE))
        val entities = remoteCatalogs.map { it.toEntity() }
        appCatalogDao.insertAllCatalogs(entities)
        remoteCatalogs.map { it.toModel() }
    }

    override suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel> = runCatching {
        val appCatalogDto = appCatalogsDataSource.insertAndGetCatalog(catalog = catalog.toDtoForInsert())
        appCatalogDao.insertCatalog(catalog = appCatalogDto.toEntity())
        appCatalogDto.toModel()
    }

    override suspend fun getActiveProductsWithDetails(): Result<List<ProductWithDetailsModel>> = runCatching {
        val remoteProducts = productsDataSource.getActiveProductsWithDetails()
        val appCatalogsEntities = remoteProducts.flatMap { product ->
            listOfNotNull(
                product.category?.toEntity(),
                product.unitType?.toEntity()
            )
        }.distinctBy { it.id }
        val productEntities = remoteProducts.map { it.toEntity() }
        appCatalogDao.insertAllCatalogs(appCatalogsEntities)
        productDao.upsertProducts(productEntities)
        // Retorno de solo lectura directa desde Room (no Flow)
        productDao.getActiveProductsWithDetails().map { it.toModel() }
    }

    override suspend fun savePrescription(
        consultationId: String,
        prescription: PrescriptionModel,
        items: List<PrescriptionItemModel>
    ): Result<PrescriptionWithDetailsModel> = runCatching {
        val request = CreatePrescriptionRequest(
            prescriptionData = prescription.toDtoForInsert(),
            itemsData = items.map { it.toDtoForInsert() }
        )
        val insertedDto = prescriptionsDataSource.insertPrescriptionWithDetails(request)

        appDatabase.withTransaction {
            prescriptionDao.savePrescriptionWithDetails(
                prescription = insertedDto.toEntity(),
                items = insertedDto.items.map { it.toEntity() }
            )
            consultationStepProgressDao.upsertSingleProgress(
                ConsultationStepProgressEntity(
                    consultationId = consultationId,
                    stepCatalogId = Constants.PRESCRIPTION_STEP_DIAGNOSIS,
                    recordId = insertedDto.id,
                    isCompleted = true,
                    status = Constants.ACTIVE_STATUS
                )
            )
        }
        insertedDto.toWithDetailsModel()
    }

    override suspend fun updatePrescription(
        consultationId: String,
        prescription: PrescriptionModel,
        items: List<PrescriptionItemModel>
    ): Result<PrescriptionWithDetailsModel> = runCatching {
        val request = UpdatePrescriptionRequest(
            prescriptionData = prescription.toDtoForUpdate(),
            itemsData = items.map { it.toDtoForInsert() }
        )
        // Consumir el RPC en Supabase
        val updatedDto = prescriptionsDataSource.updatePrescriptionWithDetails(request)

        // Sincronización atómica en Room
        appDatabase.withTransaction {
            prescriptionDao.savePrescriptionWithDetails(
                prescription = updatedDto.toEntity(),
                items = updatedDto.items.map { it.toEntity() }
            )
        }
        updatedDto.toWithDetailsModel()
    }

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(consultationId)
            ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getPrescriptionWithDetailsByConsultationId(consultationId: String): Result<PrescriptionWithDetailsModel?> = runCatching {
        val localPrescription = prescriptionDao.getPrescriptionWithDetailsByConsultationId(consultationId)
        if (localPrescription != null) {
            return@runCatching localPrescription.toModel()
        }
        val remoteDto = prescriptionsDataSource.getPrescriptionWithDetailsByConsultationId(consultationId) ?: return@runCatching null
        appDatabase.withTransaction {
            prescriptionDao.savePrescriptionWithDetails(
                prescription = remoteDto.toEntity(),
                items = remoteDto.items.map { it.toEntity() }
            )
        }
        prescriptionDao.getPrescriptionWithDetailsByConsultationId(consultationId)?.toModel()
    }
}