package yosel.dev.atti.screens.detail_consultation.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ConsultationStepProgressModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

interface DetailConsultationRepository {
    fun getConsultationWithDetailsFlow(consultationId: String): Flow<ConsultationWithDetailsModel?>
    fun getConsultationStepsProgressFlow(consultationId: String, consultationTypeId: Int): Flow<List<ConsultationStepProgressModel>>
    suspend fun syncConsultationSteps(consultationId: String, consultationTypeId: Int): Result<Unit>
}