package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClinicalExaminationDto(
    @SerialName("id") val id: String? = null,
    @SerialName("consultation_id") val consultationId: String,
    @SerialName("mucous_membranes") val mucousMembranes: String? = null,
    @SerialName("coat_catalog_id") val coatCatalogId: Int? = null,
    @SerialName("abdominal_palpation") val abdominalPalpation: String? = null,
    @SerialName("body_condition") val bodyCondition: Int? = null,
    @SerialName("other_findings") val otherFindings: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int = 1,
    // Relaciones opcionales desde Supabase
    @SerialName("coat") val coat: AppCatalogDto? = null,
    @SerialName("lymph_nodes") val lymphNodes: List<ClinicalExamLymphNodeDto> = emptyList()
)
