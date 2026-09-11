package yosel.dev.atti.core.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import yosel.dev.atti.core.models.dto.PreAnestheticTestDto

@Serializable
data class ReplacePreAnestheticTestsRequest(
    @SerialName("p_consultation_id")
    val consultationId: String,
    @SerialName("p_tests")
    val tests: List<PreAnestheticTestDto>
)
