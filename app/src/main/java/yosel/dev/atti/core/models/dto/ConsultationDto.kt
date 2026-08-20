package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsultationDto(
    @SerialName("id") val id: String? = null,
    @SerialName("patient_id") val patientId: String,
    @SerialName("consultation_type_id") val consultationTypeId: Int? = null,
    @SerialName("started_at") val startedAt: String? = null,
    @SerialName("completed_at") val completedAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int = 1,
    // Relaciones opcionales desde Supabase
    @SerialName("patient") val patient: PatientDto? = null,
    @SerialName("consultation_type") val consultationType: AppCatalogDto? = null
)
