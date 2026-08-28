package yosel.dev.atti.core.models.model

data class ClinicalExamLymphNodeWithDetailsModel(
    val lymphNode: ClinicalExamLymphNodeModel = ClinicalExamLymphNodeModel(),
    val catalog: AppCatalogModel = AppCatalogModel()
)
