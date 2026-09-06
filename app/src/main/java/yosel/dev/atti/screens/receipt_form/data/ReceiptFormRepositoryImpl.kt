package yosel.dev.atti.screens.receipt_form.data

import androidx.room.withTransaction
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PrescriptionItemModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ReceiptItemModel
import yosel.dev.atti.core.models.model.ReceiptModel
import yosel.dev.atti.core.models.model.ReceiptWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.TreatmentModel
import yosel.dev.atti.core.models.request.CreateReceiptRequest
import yosel.dev.atti.core.models.request.UpdateReceiptRequest
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.room.tables.prescription.PrescriptionDao
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.receipt.ReceiptDao
import yosel.dev.atti.core.room.tables.service.ServiceDao
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyDao
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.room.tables.treatment.TreatmentDao
import yosel.dev.atti.core.supabase.PrescriptionsDataSource
import yosel.dev.atti.core.supabase.ProductsDataSource
import yosel.dev.atti.core.supabase.ReceiptsDataSource
import yosel.dev.atti.core.supabase.ServicesDataSource
import yosel.dev.atti.core.supabase.TreatmentsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.core.utils.toWithDetailsModel
import yosel.dev.atti.screens.receipt_form.domain.ReceiptFormRepository
import javax.inject.Inject

class ReceiptFormRepositoryImpl @Inject constructor(
    private val consultationDao: ConsultationDao,
    private val productsDataSource: ProductsDataSource,
    private val productDao: ProductDao,
    private val supplierDao: SupplierDao,
    private val servicesDataSource: ServicesDataSource,
    private val serviceDao: ServiceDao,
    private val serviceSupplyDao: ServiceSupplyDao,
    private val appCatalogDao: AppCatalogDao,
    private val treatmentsDataSource: TreatmentsDataSource,
    private val treatmentDao: TreatmentDao,
    private val prescriptionsDataSource: PrescriptionsDataSource,
    private val prescriptionDao: PrescriptionDao,
    private val receiptsDataSource: ReceiptsDataSource,
    private val receiptDao: ReceiptDao,
    private val appDatabase: AppDatabase,
    private val consultationStepProgressDao: ConsultationStepProgressDao,
) : ReceiptFormRepository {

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(consultationId)
            ?: throw IllegalStateException("No se pudo recuperar la información de la consulta.")
        consultationEntity.toModel()
    }

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

    override suspend fun getTreatmentsByConsultationId(consultationId: String): Result<List<TreatmentModel>> = runCatching {
        val remote = treatmentsDataSource.getTreatmentsByConsultationId(consultationId)

        val entities = remote.map { it.toEntity() }
        if (entities.isNotEmpty()) {
            treatmentDao.upsertTreatments(entities)
        }

        treatmentDao.getTreatmentsByConsultationId(consultationId).map { it.toModel() }
    }

    override suspend fun getPrescriptionItemsByConsultationId(consultationId: String): Result<List<PrescriptionItemModel>> = runCatching {
        val remote = prescriptionsDataSource.getPrescriptionWithDetailsByConsultationId(consultationId)

        val entity = remote?.toEntity()
        val entities = remote?.items?.map { it.toEntity() }

        if (entity !=null){
            prescriptionDao.upsertPrescription(entity)
        }
        if (entities != null){
            if (entities.isNotEmpty()) {
                val entitiesWithProductId = entities.filter { it.productId != null }
                prescriptionDao.upsertPrescriptionItems(entitiesWithProductId)
            }
        }

        prescriptionDao.getPrescriptionItemsByConsultationId(consultationId).map { it.toModel() }

    }

    override suspend fun saveReceipt(
        consultationId: String?,
        receipt: ReceiptModel,
        items: List<ReceiptItemModel>
    ): Result<ReceiptWithDetailsModel> = runCatching {
        val request = CreateReceiptRequest(
            receiptData = receipt.toDtoForInsert(),
            itemsData = items.map { it.toDtoForInsert() }
        )
        val insertedDto = receiptsDataSource.insertReceiptWithDetails(request)
        appDatabase.withTransaction {
            receiptDao.saveReceiptWithDetails(
                receipt = insertedDto.toEntity(),
                items = insertedDto.items.map { it.toEntity() }
            )
            if (consultationId != null){
                consultationStepProgressDao.upsertSingleProgress(
                    ConsultationStepProgressEntity(
                        consultationId = consultationId,
                        stepCatalogId = Constants.RECEIPT_STEP_DIAGNOSIS,
                        recordId = insertedDto.id.toString(),
                        isCompleted = true,
                        status = Constants.ACTIVE_STATUS
                    )
                )
            }
        }
        insertedDto.toWithDetailsModel()
    }

    override suspend fun updateReceipt(
        receipt: ReceiptModel,
        items: List<ReceiptItemModel>
    ): Result<ReceiptWithDetailsModel> = runCatching {
        val request = UpdateReceiptRequest(
            receiptData = receipt.toDtoForUpdate(),
            itemsData = items.map { it.toDtoForInsert() }
        )
        val updatedDto = receiptsDataSource.updateReceiptWithDetails(request)
        appDatabase.withTransaction {
            receiptDao.saveReceiptWithDetails(
                receipt = updatedDto.toEntity(),
                items = updatedDto.items.map { it.toEntity() }
            )
        }
        updatedDto.toWithDetailsModel()
    }

    override suspend fun getReceiptWithDetailsById(receiptId: String): Result<ReceiptWithDetailsModel?> = runCatching {
        val local = receiptDao.getReceiptWithDetailsById(receiptId)
        if (local != null) return@runCatching local.toModel()

        val remoteDto = receiptsDataSource.getReceiptWithDetailsById(receiptId) ?: return@runCatching null
        appDatabase.withTransaction {
            receiptDao.saveReceiptWithDetails(
                receipt = remoteDto.toEntity(),
                items = remoteDto.items.map { it.toEntity() }
            )
        }
        receiptDao.getReceiptWithDetailsById(receiptId)?.toModel()
    }

    override suspend fun getReceiptWithDetailsByConsultationId(consultationId: String): Result<ReceiptWithDetailsModel?> = runCatching {
        val local = receiptDao.getReceiptWithDetailsByConsultationId(consultationId)
        if (local != null) return@runCatching local.toModel()

        val remoteDto = receiptsDataSource.getReceiptWithDetailsByConsultationId(consultationId) ?: return@runCatching null
        appDatabase.withTransaction {
            receiptDao.saveReceiptWithDetails(
                receipt = remoteDto.toEntity(),
                items = remoteDto.items.map { it.toEntity() }
            )
        }
        receiptDao.getReceiptWithDetailsByConsultationId(consultationId)?.toModel()
    }
}