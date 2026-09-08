package yosel.dev.atti.screens.treatment_form.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.TreatmentModel
import yosel.dev.atti.core.models.model.TreatmentWithDetailsModel

interface TreatmentFormRepository {
    fun getActiveProductsWithDetailsFlow(): Flow<List<ProductWithDetailsModel>>
    fun getActiveServicesWithDetailsFlow(): Flow<List<ServiceWithDetailsModel>>
    suspend fun syncProducts(): Result<Unit>
    suspend fun syncServices(): Result<Unit>
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