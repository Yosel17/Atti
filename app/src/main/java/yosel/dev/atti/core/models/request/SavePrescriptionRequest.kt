package yosel.dev.atti.core.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import yosel.dev.atti.core.models.dto.PrescriptionDto
import yosel.dev.atti.core.models.dto.PrescriptionItemDto

@Serializable
data class SavePrescriptionRequest(
    @SerialName("prescription_data")
    val prescriptionData: PrescriptionDto,
    @SerialName("items_data")
    val itemsData: List<PrescriptionItemDto> = emptyList()
)
