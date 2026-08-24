package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnamnesisVaccineDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("anamnesis_id") val anamnesisId: String,
    @SerialName("application_date") val applicationDate: String? = null,
    @SerialName("vaccine_catalog_id") val vaccineCatalogId: Int,
    @SerialName("scheme_catalog_id") val schemeCatalogId: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    // Relaciones opcionales
    @SerialName("vaccine") val vaccine: AppCatalogDto? = null,
    @SerialName("scheme") val scheme: AppCatalogDto? = null
)
