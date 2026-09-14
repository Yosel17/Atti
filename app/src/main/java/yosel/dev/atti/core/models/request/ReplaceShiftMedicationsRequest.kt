package yosel.dev.atti.core.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import yosel.dev.atti.core.models.dto.ShiftMedicationDto

@Serializable
data class ReplaceShiftMedicationsRequest(
    @SerialName("p_consultation_id")
    val consultationId: String,
    @SerialName("p_medications")
    val medications: List<ShiftMedicationDto>
)
