package yosel.dev.atti.core.models.model

data class ClinicalExamWithDetailsModel(
    val clinicalExam: ClinicalExaminationModel = ClinicalExaminationModel(),
    val coat: AppCatalogModel = AppCatalogModel(),
    val lymphNodes: List<ClinicalExamLymphNodeWithDetailsModel> = emptyList()
)
