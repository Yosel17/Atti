package yosel.dev.atti.screens.fasting_form.domain

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.FastingWithDetailsModel

interface FastingFormRepository {
    suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>>
    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
    suspend fun saveFasting(
        consultationId: String,
        foodFastingId: Int,
        waterFastingId: Int
    ): Result<FastingWithDetailsModel>
    suspend fun updateFasting(
        id: String,
        consultationId: String,
        foodFastingId: Int,
        waterFastingId: Int
    ): Result<FastingWithDetailsModel>
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getFastingByConsultationId(consultationId: String): Result<FastingWithDetailsModel?>
}