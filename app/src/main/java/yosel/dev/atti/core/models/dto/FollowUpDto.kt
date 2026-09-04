package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FollowUpDto(
    @SerialName("id") val id: String? = null,
    @SerialName("consultation_id") val consultationId: String,
    @SerialName("patient_id") val patientId: String,
    @SerialName("scheduled_at") val scheduledAt: String,
    @SerialName("reason") val reason: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int = 1,

    // Relaciones opcionales desde Supabase (Joins)
    @SerialName("patient") val patient: PatientDto? = null,
    @SerialName("consultation") val consultation: ConsultationDto? = null
)
