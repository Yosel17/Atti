package yosel.dev.atti.screens.navigation_bar.consultation.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel

interface ConsultationRepository {
    fun getActiveConsultationFlow(): Flow<ConsultationWithDetailsModel?>
    suspend fun syncActiveConsultation(): Result<Unit>
    fun getAllPatientsWithCatalogsFlow(): Flow<List<PatientWithCatalogsModel>>
    suspend fun syncPatients(): Result<Unit>
    suspend fun getConsultationReasons(): Result<List<AppCatalogModel>>
    suspend fun createConsultation(patientId: String, consultationTypeId: Int): Result<ConsultationModel>
}