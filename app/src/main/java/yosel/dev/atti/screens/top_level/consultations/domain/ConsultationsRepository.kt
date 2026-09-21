package yosel.dev.atti.screens.top_level.consultations.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

interface ConsultationsRepository {
    fun getAllConsultationsWithDetails(): Flow<List<ConsultationWithDetailsModel>>
    suspend fun syncConsultations(): Result<Unit>
}