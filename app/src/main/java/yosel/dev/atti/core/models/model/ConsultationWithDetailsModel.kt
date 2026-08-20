package yosel.dev.atti.core.models.model

data class ConsultationWithDetailsModel(
    val consultation: ConsultationModel = ConsultationModel(),
    val patient: PatientModel = PatientModel(),
    val consultationType: AppCatalogModel = AppCatalogModel()
)
