package yosel.dev.atti.screens.asa_classification_form.domain

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.AsaClassificationWithDetailsModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

interface AsaClassificationFormRepository {
    suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>>
    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
    suspend fun saveAsaClassifications(
        consultationId: String,
        selectedCatalogs: List<AppCatalogModel>
    ): Result<List<AsaClassificationWithDetailsModel>>
    suspend fun updateAsaClassifications(
        consultationId: String,
        selectedCatalogs: List<AppCatalogModel>
    ): Result<List<AsaClassificationWithDetailsModel>>
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getAsaClassificationsByConsultationId(consultationId: String): Result<List<AsaClassificationWithDetailsModel>>
}
