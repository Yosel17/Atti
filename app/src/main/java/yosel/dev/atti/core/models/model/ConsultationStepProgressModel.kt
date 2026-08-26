package yosel.dev.atti.core.models.model

data class ConsultationStepProgressModel(
    val typeStep: ConsultationTypeStepModel = ConsultationTypeStepModel(),
    val stepCatalog: AppCatalogModel = AppCatalogModel(),
    val isCompleted: Boolean = false,
    val recordId: String? = null
)
