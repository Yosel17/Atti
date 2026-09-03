package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ObservationDto(
    @SerialName("id") val id: String? = null,
    @SerialName("consultation_id") val consultationId: String,
    @SerialName("observation") val observation: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int = 1
)
