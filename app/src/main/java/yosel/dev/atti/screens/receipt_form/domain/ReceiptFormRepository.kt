package yosel.dev.atti.screens.receipt_form.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PrescriptionItemModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ReceiptItemModel
import yosel.dev.atti.core.models.model.ReceiptModel
import yosel.dev.atti.core.models.model.ReceiptWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.TreatmentModel

interface ReceiptFormRepository {
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    fun getActiveProductsWithDetailsFlow(): Flow<List<ProductWithDetailsModel>>
    fun getActiveServicesWithDetailsFlow(): Flow<List<ServiceWithDetailsModel>>
    suspend fun syncProducts(): Result<Unit>
    suspend fun syncServices(): Result<Unit>
    suspend fun getTreatmentsByConsultationId(consultationId: String): Result<List<TreatmentModel>>
    suspend fun getPrescriptionItemsByConsultationId(consultationId: String): Result<List<PrescriptionItemModel>>
    suspend fun saveReceipt(
        consultationId: String?,
        receipt: ReceiptModel,
        items: List<ReceiptItemModel>
    ): Result<ReceiptWithDetailsModel>
    suspend fun updateReceipt(
        receipt: ReceiptModel,
        items: List<ReceiptItemModel>
    ): Result<ReceiptWithDetailsModel>
    suspend fun getReceiptWithDetailsById(receiptId: String): Result<ReceiptWithDetailsModel?>
    suspend fun getReceiptWithDetailsByConsultationId(consultationId: String): Result<ReceiptWithDetailsModel?>
}