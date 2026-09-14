package yosel.dev.atti.screens.shift_medication_form.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.ShiftMedicationModel
import yosel.dev.atti.core.models.model.ShiftMedicationWithDetailsModel

interface ShiftMedicationFormRepository {
    fun getActiveProductsWithDetailsFlow(): Flow<List<ProductWithDetailsModel>>
    fun getActiveServicesWithDetailsFlow(): Flow<List<ServiceWithDetailsModel>>
    suspend fun syncProducts(): Result<Unit>
    suspend fun syncServices(): Result<Unit>
    suspend fun saveShiftMedications(
        consultationId: String,
        medications: List<ShiftMedicationModel>
    ): Result<List<ShiftMedicationWithDetailsModel>>
    suspend fun updateShiftMedications(
        consultationId: String,
        medications: List<ShiftMedicationModel>
    ): Result<List<ShiftMedicationWithDetailsModel>>
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getShiftMedicationsByConsultationId(consultationId: String): Result<List<ShiftMedicationWithDetailsModel>>
}
