package yosel.dev.atti.screens.treatment_form.domain

import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.TreatmentModel
import yosel.dev.atti.core.models.model.TreatmentWithDetailsModel

interface TreatmentFormRepository {
    suspend fun getActiveProductsWithDetails(): Result<List<ProductWithDetailsModel>>
    suspend fun getActiveServicesWithDetails(): Result<List<ServiceWithDetailsModel>>
    suspend fun saveTreatments(
        consultationId: String,
        treatments: List<TreatmentModel>
    ): Result<List<TreatmentWithDetailsModel>>
    suspend fun updateTreatments(
        consultationId: String,
        treatments: List<TreatmentModel>
    ): Result<List<TreatmentWithDetailsModel>>
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getTreatmentsByConsultationId(consultationId: String): Result<List<TreatmentWithDetailsModel>>
}