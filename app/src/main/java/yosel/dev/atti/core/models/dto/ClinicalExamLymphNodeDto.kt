package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClinicalExamLymphNodeDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("clinical_examination_id") val clinicalExaminationId: String,
    @SerialName("catalog_id") val catalogId: Int,
    @SerialName("created_at") val createdAt: String? = null,
    // Relación opcional
    @SerialName("catalog") val catalog: AppCatalogDto? = null
)
