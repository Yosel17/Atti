package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FastingDto(
    @SerialName("id") val id: String? = null,
    @SerialName("consultation_id") val consultationId: String,
    @SerialName("food_fasting_catalog_id") val foodFastingCatalogId: Int,
    @SerialName("water_fasting_catalog_id") val waterFastingCatalogId: Int,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int = 1,

    // Relaciones mapeadas desde Supabase (Joins)
    @SerialName("food_fasting") val foodFasting: AppCatalogDto? = null,
    @SerialName("water_fasting") val waterFasting: AppCatalogDto? = null
)
