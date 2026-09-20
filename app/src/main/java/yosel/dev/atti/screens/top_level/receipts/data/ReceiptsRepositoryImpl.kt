package yosel.dev.atti.screens.top_level.receipts.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.ReceiptWithDetailsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.room.tables.product.ProductDao
import yosel.dev.atti.core.room.tables.receipt.ReceiptDao
import yosel.dev.atti.core.room.tables.service.ServiceDao
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.supabase.ReceiptsDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.top_level.receipts.domain.ReceiptsRepository
import javax.inject.Inject

class ReceiptsRepositoryImpl @Inject constructor(
    private val receiptDao: ReceiptDao,
    private val receiptsDataSource: ReceiptsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val clientDao: ClientDao,
    private val patientDao: PatientDao,
    private val consultationDao: ConsultationDao,
    private val supplierDao: SupplierDao,
    private val productDao: ProductDao,
    private val serviceDao: ServiceDao
) : ReceiptsRepository {

    override fun getAllReceiptsWithDetails(): Flow<List<ReceiptWithDetailsModel>> =
        receiptDao.getAllReceiptsWithDetailsFlow()
            .map { entities -> entities.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    override suspend fun syncReceipts(): Result<Unit> = runCatching {
        val remoteReceipts = receiptsDataSource.getAllReceiptsWithDetails()

        // 1. Extraer catálogos secundarios (tipos de consulta, especies, géneros, categorías)
        val catalogEntities = remoteReceipts.flatMap { receiptDto ->
            val consultationCatalogs = listOfNotNull(
                receiptDto.consultation?.consultationType?.toEntity(),
                receiptDto.consultation?.patient?.species?.toEntity(),
                receiptDto.consultation?.patient?.gender?.toEntity()
            )
            val itemCatalogs = receiptDto.items.flatMap { itemDto ->
                listOfNotNull(
                    itemDto.product?.category?.toEntity(),
                    itemDto.product?.unitType?.toEntity(),
                    itemDto.service?.category?.toEntity()
                )
            }
            consultationCatalogs + itemCatalogs
        }.distinctBy { it.id }

        // 2. Clientes y Pacientes vinculados
        val clientEntities = remoteReceipts.mapNotNull {
            it.consultation?.patient?.client?.toEntity()
        }.distinctBy { it.id }

        val patientEntities = remoteReceipts.mapNotNull {
            it.consultation?.patient?.toEntity()
        }.distinctBy { it.id }

        // 3. Consultas
        val consultationEntities = remoteReceipts.mapNotNull {
            it.consultation?.toEntity()
        }.distinctBy { it.id }

        // 4. Proveedores, Productos y Servicios vinculados en los ítems
        val supplierEntities = remoteReceipts.flatMap { receipt ->
            receipt.items.mapNotNull { it.product?.supplier?.toEntity() }
        }.distinctBy { it.id }

        val productEntities = remoteReceipts.flatMap { receipt ->
            receipt.items.mapNotNull { it.product?.toEntity() }
        }.distinctBy { it.id }

        val serviceEntities = remoteReceipts.flatMap { receipt ->
            receipt.items.mapNotNull { it.service?.toEntity() }
        }.distinctBy { it.id }

        // 5. Recibos e ítems
        val receiptEntities = remoteReceipts.map { it.toEntity() }
        val receiptItemEntities = remoteReceipts.flatMap { receipt ->
            receipt.items.map { it.toEntity() }
        }.distinctBy { it.id }

        // Inserciones en Room manteniendo consistencia de llaves foráneas
        if (catalogEntities.isNotEmpty()) appCatalogDao.insertAllCatalogs(catalogEntities)
        if (clientEntities.isNotEmpty()) clientDao.upsertClients(clientEntities)
        if (patientEntities.isNotEmpty()) patientDao.upsertPatients(patientEntities)
        if (consultationEntities.isNotEmpty()) consultationDao.upsertConsultations(consultationEntities)
        if (supplierEntities.isNotEmpty()) supplierDao.upsertSuppliers(supplierEntities)
        if (productEntities.isNotEmpty()) productDao.upsertProducts(productEntities)
        if (serviceEntities.isNotEmpty()) serviceDao.upsertServices(serviceEntities)

        receiptDao.upsertReceipts(receiptEntities)
        if (receiptItemEntities.isNotEmpty()) {
            receiptDao.upsertReceiptItems(receiptItemEntities)
        }
    }
}