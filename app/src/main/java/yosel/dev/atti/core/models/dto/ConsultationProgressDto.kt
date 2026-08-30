package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StepReferenceDto(
    @SerialName("id") val id: String? = null,
    @SerialName("status") val status: Int = 1
)

@Serializable
data class ConsultationProgressDto(
    @SerialName("id") val id: String,
    @SerialName("status") val status: Int = 1,
    @SerialName("consultation_type_id") val consultationTypeId: Int? = null,
    @SerialName("anamnesis") val anamnesis: List<StepReferenceDto> = emptyList(),
    @SerialName("clinical_examinations") val clinicalExaminations: List<StepReferenceDto> = emptyList(),
    @SerialName("physiological_constants") val physiologicalConstants: List<StepReferenceDto> = emptyList()
)
