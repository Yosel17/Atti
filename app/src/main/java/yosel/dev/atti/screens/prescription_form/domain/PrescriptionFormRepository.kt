package yosel.dev.atti.screens.prescription_form.domain

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PrescriptionItemModel
import yosel.dev.atti.core.models.model.PrescriptionModel
import yosel.dev.atti.core.models.model.PrescriptionWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel

interface PrescriptionFormRepository {
    suspend fun getPresetCatalogs(): Result<List<AppCatalogModel>>
    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
    suspend fun getActiveProductsWithDetails(): Result<List<ProductWithDetailsModel>>
    suspend fun savePrescription(
        consultationId: String,
        prescription: PrescriptionModel,
        items: List<PrescriptionItemModel>
    ): Result<PrescriptionWithDetailsModel>
    suspend fun updatePrescription(
        consultationId: String,
        prescription: PrescriptionModel,
        items: List<PrescriptionItemModel>
    ): Result<PrescriptionWithDetailsModel>
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getPrescriptionWithDetailsByConsultationId(consultationId: String): Result<PrescriptionWithDetailsModel?>
}