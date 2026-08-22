package yosel.dev.atti.screens.detail_consultation.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

interface DetailConsultationRepository {

    fun getConsultationWithDetailsFlow(consultationId: String): Flow<ConsultationWithDetailsModel?>

    suspend fun getConsultationSteps(): Result<List<AppCatalogModel>>
}