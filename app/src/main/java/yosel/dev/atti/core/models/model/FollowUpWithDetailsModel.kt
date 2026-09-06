package yosel.dev.atti.core.models.model

data class FollowUpWithDetailsModel(
    val followUp: FollowUpModel = FollowUpModel(),
    val patientWithDetails: PatientWithDetailsModel = PatientWithDetailsModel(),
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel()
)
