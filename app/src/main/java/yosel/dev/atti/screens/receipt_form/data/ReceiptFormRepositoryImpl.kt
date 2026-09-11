package yosel.dev.atti.screens.receipt_form.data

import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PreAnestheticTestModel
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
import yosel.dev.atti.core.room.tables.pre_anesthetic_test.PreAnestheticTestDao
import yosel.dev.atti.core.room.tables.prescription.PrescriptionDao
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.receipt.ReceiptDao
import yosel.dev.atti.core.room.tables.receipt.ReceiptItemEntity
import yosel.dev.atti.core.room.tables.service.ServiceDao
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyDao
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.room.tables.treatment.TreatmentDao
import yosel.dev.atti.core.supabase.PreAnestheticTestsDataSource
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
    private val preAnestheticTestsDataSource: PreAnestheticTestsDataSource,
    private val preAnestheticTestDao: PreAnestheticTestDao,
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
                prescriptionDao.upsertPrescriptionItems(entities)
            }
        }

        prescriptionDao.getPrescriptionItemsByConsultationId(consultationId).map { it.toModel() }

    }

    override suspend fun getPreAnestheticTestsByConsultationId(consultationId: String): Result<List<PreAnestheticTestModel>> = runCatching {
        val remote = preAnestheticTestsDataSource.getPreAnestheticTestsByConsultationId(consultationId)

        val entities = remote.map { it.toEntity() }
        if (entities.isNotEmpty()) {
            preAnestheticTestDao.upsertPreAnestheticTests(entities)
        }

        preAnestheticTestDao.getPreAnestheticTestsByConsultationId(consultationId).map { it.toModel() }
    }

    override suspend fun saveReceipt(
        consultationId: String?,
        receipt: ReceiptModel,
        items: List<ReceiptItemModel>
    ): Result<ReceiptWithDetailsModel> = runCatching {
        validateStockForNewReceipt(items)

        val request = CreateReceiptRequest(
            receiptData = receipt.toDtoForInsert(),
            itemsData = items.map { it.toDtoForInsert() }
        )
        val insertedDto = receiptsDataSource.insertReceiptWithDetails(request)
        val newItemsEntities = insertedDto.items.map { it.toEntity() }.ifEmpty { items.map { it.toEntity() } }
        appDatabase.withTransaction {
            receiptDao.saveReceiptWithDetails(
                receipt = insertedDto.toEntity(),
                items = newItemsEntities
            )
            deductStockForReceiptItems(newItemsEntities)

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
        val receiptId = receipt.id.ifBlank { error("El ID del recibo es requerido para actualizar.") }
        validateStockForUpdateReceipt(receiptId, items)

        val request = UpdateReceiptRequest(
            receiptData = receipt.toDtoForUpdate(),
            itemsData = items.map { it.toDtoForInsert() }
        )
        val updatedDto = receiptsDataSource.updateReceiptWithDetails(request)
        val finalReceiptId = updatedDto.id ?: receiptId
        val newItemsEntities = updatedDto.items.map { it.toEntity() }.ifEmpty { items.map { it.toEntity() } }
        appDatabase.withTransaction {
            val previousItems = receiptDao.getReceiptItemsByReceiptId(finalReceiptId)
            restoreStockForReceiptItems(previousItems)

            receiptDao.saveReceiptWithDetails(
                receipt = updatedDto.toEntity(),
                items = newItemsEntities
            )
            deductStockForReceiptItems(newItemsEntities)
        }
        updatedDto.toWithDetailsModel()
    }

    private suspend fun validateStockForNewReceipt(items: List<ReceiptItemModel>) {
        for (item in items) {
            val qty = item.quantity.roundToInt()
            if (!item.productId.isNullOrBlank()) {
                val product = productDao.getProductById(item.productId)
                    ?: throw IllegalStateException("El producto seleccionado no existe en la base de datos local.")
                if (product.stock < qty) {
                    throw IllegalStateException("Stock insuficiente para '${product.commercialName}'. Stock disponible: ${product.stock}, solicitado: $qty.")
                }
            } else if (!item.serviceId.isNullOrBlank()) {
                val supplies = serviceSupplyDao.getSuppliesByServiceId(item.serviceId)
                for (supply in supplies) {
                    val requiredQty = (supply.quantityRequired * item.quantity).roundToInt()
                    if (requiredQty > 0) {
                        val product = productDao.getProductById(supply.productId)
                            ?: throw IllegalStateException("El producto del suministro no existe en la base de datos local.")
                        if (product.stock < requiredQty) {
                            throw IllegalStateException("Stock insuficiente de '${product.commercialName}' para el servicio. Stock disponible: ${product.stock}, solicitado: $requiredQty.")
                        }
                    }
                }
            }
        }
    }

    private suspend fun validateStockForUpdateReceipt(
        receiptId: String,
        newItems: List<ReceiptItemModel>
    ) {
        val previousItems = receiptDao.getReceiptItemsByReceiptId(receiptId)
        val restoredStockMap = mutableMapOf<String, Int>()

        for (prev in previousItems) {
            val qty = prev.quantity.roundToInt()
            if (!prev.productId.isNullOrBlank()) {
                restoredStockMap[prev.productId] = (restoredStockMap[prev.productId] ?: 0) + qty
            } else if (!prev.serviceId.isNullOrBlank()) {
                val supplies = serviceSupplyDao.getSuppliesByServiceId(prev.serviceId)
                for (supply in supplies) {
                    val req = (supply.quantityRequired * prev.quantity).roundToInt()
                    restoredStockMap[supply.productId] = (restoredStockMap[supply.productId] ?: 0) + req
                }
            }
        }

        for (item in newItems) {
            val qty = item.quantity.roundToInt()
            if (!item.productId.isNullOrBlank()) {
                val product = productDao.getProductById(item.productId)
                    ?: throw IllegalStateException("El producto seleccionado no existe en la base de datos local.")
                val restored = restoredStockMap[item.productId] ?: 0
                val available = product.stock + restored
                if (available < qty) {
                    throw IllegalStateException("Stock insuficiente para '${product.commercialName}'. Stock disponible: $available, solicitado: $qty.")
                }
            } else if (!item.serviceId.isNullOrBlank()) {
                val supplies = serviceSupplyDao.getSuppliesByServiceId(item.serviceId)
                for (supply in supplies) {
                    val requiredQty = (supply.quantityRequired * item.quantity).roundToInt()
                    if (requiredQty > 0) {
                        val product = productDao.getProductById(supply.productId)
                            ?: throw IllegalStateException("El producto del suministro no existe en la base de datos local.")
                        val restored = restoredStockMap[supply.productId] ?: 0
                        val available = product.stock + restored
                        if (available < requiredQty) {
                            throw IllegalStateException("Stock insuficiente de '${product.commercialName}' para el servicio. Stock disponible: $available, solicitado: $requiredQty.")
                        }
                    }
                }
            }
        }
    }

    private suspend fun restoreStockForReceiptItems(items: List<ReceiptItemEntity>) {
        for (item in items) {
            val qty = item.quantity.roundToInt()
            if (!item.productId.isNullOrBlank()) {
                if (qty > 0) {
                    productDao.increaseStock(item.productId, qty)
                }
            } else if (!item.serviceId.isNullOrBlank()) {
                val supplies = serviceSupplyDao.getSuppliesByServiceId(item.serviceId)
                for (supply in supplies) {
                    val requiredQty = (supply.quantityRequired * item.quantity).roundToInt()
                    if (requiredQty > 0) {
                        productDao.increaseStock(supply.productId, requiredQty)
                    }
                }
            }
        }
    }

    private suspend fun deductStockForReceiptItems(items: List<ReceiptItemEntity>) {
        for (item in items) {
            val qty = item.quantity.roundToInt()
            if (!item.productId.isNullOrBlank()) {
                if (qty > 0) {
                    productDao.decreaseStock(item.productId, qty)
                }
            } else if (!item.serviceId.isNullOrBlank()) {
                val supplies = serviceSupplyDao.getSuppliesByServiceId(item.serviceId)
                for (supply in supplies) {
                    val requiredQty = (supply.quantityRequired * item.quantity).roundToInt()
                    if (requiredQty > 0) {
                        productDao.decreaseStock(supply.productId, requiredQty)
                    }
                }
            }
        }
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