package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiagnosisDto(
    @SerialName("id") val id: String? = null,
    @SerialName("consultation_id") val consultationId: String,
    @SerialName("diagnosis_catalog_id") val diagnosisCatalogId: Int,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int = 1,
    // Relación opcional con catálogo desde Supabase
    @SerialName("catalog") val catalog: AppCatalogDto? = null
)
