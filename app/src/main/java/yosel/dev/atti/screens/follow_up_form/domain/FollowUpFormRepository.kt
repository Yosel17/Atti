package yosel.dev.atti.screens.follow_up_form.domain

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.FollowUpModel
import yosel.dev.atti.core.models.model.FollowUpWithDetailsModel

interface FollowUpFormRepository {
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getFollowUpById(followUpId: String): Result<FollowUpWithDetailsModel?>
    suspend fun getFollowUpByConsultationId(consultationId: String): Result<FollowUpWithDetailsModel?>
    suspend fun getQuickReasonCatalogs(): Result<List<AppCatalogModel>>
    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
    suspend fun saveFollowUp(consultationId: String, followUp: FollowUpModel): Result<FollowUpWithDetailsModel>
    suspend fun updateFollowUp(consultationId: String, followUp: FollowUpModel): Result<FollowUpWithDetailsModel>
}