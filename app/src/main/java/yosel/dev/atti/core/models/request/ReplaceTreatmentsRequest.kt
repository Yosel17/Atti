package yosel.dev.atti.core.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import yosel.dev.atti.core.models.dto.TreatmentDto

@Serializable
data class ReplaceTreatmentsRequest(
    @SerialName("p_consultation_id")
    val consultationId: String,
    @SerialName("p_treatments")
    val treatments: List<TreatmentDto>
)
