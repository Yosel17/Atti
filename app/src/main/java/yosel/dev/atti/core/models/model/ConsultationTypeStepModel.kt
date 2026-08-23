package yosel.dev.atti.core.models.model

data class ConsultationTypeStepModel(
    val id: Int = 0,
    val consultationTypeId: Int = 0,
    val stepCatalogId: Int = 0,
    val stepOrder: Int = 1,
    val isRequired: Boolean = true
)
