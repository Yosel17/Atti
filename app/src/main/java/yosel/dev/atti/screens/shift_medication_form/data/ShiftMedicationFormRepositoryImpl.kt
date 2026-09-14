package yosel.dev.atti.screens.shift_medication_form.data

import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.ShiftMedicationModel
import yosel.dev.atti.core.models.model.ShiftMedicationWithDetailsModel
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.service.ServiceDao
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyDao
import yosel.dev.atti.core.room.tables.shift_medication.ShiftMedicationDao
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.supabase.ProductsDataSource
import yosel.dev.atti.core.supabase.ServicesDataSource
import yosel.dev.atti.core.supabase.ShiftMedicationsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.core.utils.toWithDetailsModel
import yosel.dev.atti.screens.shift_medication_form.domain.ShiftMedicationFormRepository
import javax.inject.Inject

class ShiftMedicationFormRepositoryImpl @Inject constructor(
    private val productsDataSource: ProductsDataSource,
    private val productDao: ProductDao,
    private val servicesDataSource: ServicesDataSource,
    private val serviceDao: ServiceDao,
    private val serviceSupplyDao: ServiceSupplyDao,
    private val shiftMedicationsDataSource: ShiftMedicationsDataSource,
    private val shiftMedicationDao: ShiftMedicationDao,
    private val consultationDao: ConsultationDao,
    private val consultationStepProgressDao: ConsultationStepProgressDao,
    private val appCatalogDao: AppCatalogDao,
    private val appDatabase: AppDatabase,
    private val supplierDao: SupplierDao,
) : ShiftMedicationFormRepository {

    override fun getActiveProductsWithDetailsFlow(): Flow<List<ProductWithDetailsModel>> {
        return productDao.getActiveProductsWithDetailsFlow()
            .map { entities -> entities.map { it.toModel() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getActiveServicesWithDetailsFlow(): Flow<List<ServiceWithDetailsModel>> {
        return serviceDao.getActiveServicesWithDetailsFlow()
            .map { entities -> entities.map { it.toModel() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncProducts(): Result<Unit> = runCatching {
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
    }

    override suspend fun syncServices(): Result<Unit> = runCatching {
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
    }

    override suspend fun saveShiftMedications(
        consultationId: String,
        medications: List<ShiftMedicationModel>
    ): Result<List<ShiftMedicationWithDetailsModel>> = runCatching {
        val medicationsDtos = medications.map { it.toDtoForInsert() }
        val insertedDtos = shiftMedicationsDataSource.insertShiftMedications(medicationsDtos)

        val entities = insertedDtos.map { it.toEntity() }
        shiftMedicationDao.syncShiftMedicationsForConsultation(
            consultationId = consultationId,
            medications = entities
        )

        consultationStepProgressDao.upsertSingleProgress(
            ConsultationStepProgressEntity(
                consultationId = consultationId,
                stepCatalogId = Constants.CONSULTATION_STEP_SHIFT_MEDICATION,
                recordId = insertedDtos.firstOrNull()?.id,
                isCompleted = true,
                status = Constants.ACTIVE_STATUS
            )
        )
        insertedDtos.map { it.toWithDetailsModel() }
    }

    override suspend fun updateShiftMedications(
        consultationId: String,
        medications: List<ShiftMedicationModel>
    ): Result<List<ShiftMedicationWithDetailsModel>> = runCatching {
        val medicationsDtos = medications.map { it.toDtoForInsert() }

        val insertedDtos = shiftMedicationsDataSource.replaceShiftMedicationsRpc(
            consultationId = consultationId,
            medications = medicationsDtos
        )

        appDatabase.withTransaction {
            val entities = insertedDtos.map { it.toEntity() }
            shiftMedicationDao.syncShiftMedicationsForConsultation(
                consultationId = consultationId,
                medications = entities
            )
        }

        insertedDtos.map { it.toWithDetailsModel() }
    }

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(consultationId)
            ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getShiftMedicationsByConsultationId(consultationId: String): Result<List<ShiftMedicationWithDetailsModel>> = runCatching {
        val localMedications = shiftMedicationDao.getShiftMedicationsWithDetailsByConsultationId(consultationId)
        if (localMedications.isNotEmpty()) {
            return@runCatching localMedications.map { it.toModel() }
        }
        val remoteDtos = shiftMedicationsDataSource.getShiftMedicationsByConsultationId(consultationId)
        val entities = remoteDtos.map { it.toEntity() }
        shiftMedicationDao.syncShiftMedicationsForConsultation(consultationId, entities)
        shiftMedicationDao.getShiftMedicationsWithDetailsByConsultationId(consultationId).map { it.toModel() }
    }
}
