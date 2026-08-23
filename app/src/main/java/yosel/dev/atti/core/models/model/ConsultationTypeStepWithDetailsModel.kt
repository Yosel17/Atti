package yosel.dev.atti.core.models.model

data class ConsultationTypeStepWithDetailsModel(
    val typeStep: ConsultationTypeStepModel = ConsultationTypeStepModel(),
    val consultationType: AppCatalogModel = AppCatalogModel(),
    val stepCatalog: AppCatalogModel = AppCatalogModel()
)
