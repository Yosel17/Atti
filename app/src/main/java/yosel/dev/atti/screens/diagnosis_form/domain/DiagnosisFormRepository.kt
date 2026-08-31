package yosel.dev.atti.screens.diagnosis_form.domain

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.DiagnosisWithDetailsModel

interface DiagnosisFormRepository {
    suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>>
    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
    suspend fun saveDiagnoses(
        consultationId: String,
        selectedCatalogs: List<AppCatalogModel>
    ): Result<List<DiagnosisWithDetailsModel>>
    suspend fun updateDiagnoses(
        consultationId: String,
        selectedCatalogs: List<AppCatalogModel>
    ): Result<List<DiagnosisWithDetailsModel>>
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getDiagnosesByConsultationId(consultationId: String): Result<List<DiagnosisWithDetailsModel>>
    suspend fun getDiagnosisWithDetailsById(id: String): Result<DiagnosisWithDetailsModel>
}