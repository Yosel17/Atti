package yosel.dev.atti.core.models.model

data class DiagnosisWithDetailsModel(
    val diagnosis: DiagnosisModel = DiagnosisModel(),
    val catalog: AppCatalogModel = AppCatalogModel()
)
