package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhysiologicalConstsDto(
    @SerialName("id") val id: String? = null,
    @SerialName("consultation_id") val consultationId: String,
    @SerialName("temperature") val temperature: Double? = null,
    @SerialName("heart_rate") val heartRate: Int? = null,
    @SerialName("respiratory_rate") val respiratoryRate: Int? = null,
    @SerialName("weight") val weight: Double? = null,
    @SerialName("weight_unit_catalog_id") val weightUnitCatalogId: Int? = null,
    @SerialName("capillary_refill_time") val capillaryRefillTime: Int? = null,
    @SerialName("skin_turgor") val skinTurgor: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int = 1,
    // Relación opcional con catálogos
    @SerialName("weight_unit") val weightUnit: AppCatalogDto? = null
)
