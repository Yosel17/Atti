package yosel.dev.atti.core.models.model

data class ConsultationWithDetailsModel(
    val consultation: ConsultationModel = ConsultationModel(),
    val patientWithDetails: PatientWithDetailsModel = PatientWithDetailsModel(),
    val consultationType: AppCatalogModel = AppCatalogModel()
)
