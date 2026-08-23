package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsultationTypeStepDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("consultation_type_id") val consultationTypeId: Int,
    @SerialName("step_catalog_id") val stepCatalogId: Int,
    @SerialName("step_order") val stepOrder: Int = 1,
    @SerialName("is_required") val isRequired: Boolean = true,
    // Relaciones opcionales desde Supabase
    @SerialName("consultation_type") val consultationType: AppCatalogDto? = null,
    @SerialName("step_catalog") val stepCatalog: AppCatalogDto? = null
)
