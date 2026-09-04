package yosel.dev.atti.core.models.model

data class FollowUpWithDetailsModel(
    val followUp: FollowUpModel = FollowUpModel(),
    val patientWithDetails: PatientWithCatalogsModel = PatientWithCatalogsModel(),
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel()
)
