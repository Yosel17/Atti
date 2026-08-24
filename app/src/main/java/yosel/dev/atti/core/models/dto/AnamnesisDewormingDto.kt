package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class AnamnesisDewormingDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("anamnesis_id") val anamnesisId: String,
    @SerialName("application_date") val applicationDate: String? = null,
    @SerialName("deworming_type") val dewormingType: String,
    @SerialName("product_catalog_id") val productCatalogId: Int,
    @SerialName("created_at") val createdAt: String? = null,
    // Relación opcional
    @SerialName("product") val product: AppCatalogDto? = null
)
