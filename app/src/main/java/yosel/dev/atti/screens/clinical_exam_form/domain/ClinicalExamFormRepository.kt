package yosel.dev.atti.screens.clinical_exam_form.domain

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClinicalExamLymphNodeModel
import yosel.dev.atti.core.models.model.ClinicalExamWithDetailsModel
import yosel.dev.atti.core.models.model.ClinicalExaminationModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

interface ClinicalExamFormRepository {
    suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>>
    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
    suspend fun saveClinicalExam(
        clinicalExam: ClinicalExaminationModel,
        lymphNodes: List<ClinicalExamLymphNodeModel>
    ): Result<ClinicalExaminationModel>
    suspend fun updateClinicalExamWithDetails(
        clinicalExam: ClinicalExaminationModel,
        lymphNodes: List<ClinicalExamLymphNodeModel>?
    ): Result<Unit>
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getClinicalExamWithDetailsById(examId: String): Result<ClinicalExamWithDetailsModel>
}