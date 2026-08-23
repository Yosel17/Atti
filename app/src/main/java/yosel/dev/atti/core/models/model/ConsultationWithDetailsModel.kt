package yosel.dev.atti.core.models.model

data class ConsultationWithDetailsModel(
    val consultation: ConsultationModel = ConsultationModel(),
    val patientWithDetails: PatientWithCatalogsModel = PatientWithCatalogsModel(),
    val consultationType: AppCatalogModel = AppCatalogModel()
)
