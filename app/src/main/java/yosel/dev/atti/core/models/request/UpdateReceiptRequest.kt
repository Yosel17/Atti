package yosel.dev.atti.core.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import yosel.dev.atti.core.models.dto.ReceiptDto
import yosel.dev.atti.core.models.dto.ReceiptItemDto

@Serializable
data class UpdateReceiptRequest(
    @SerialName("receipt_data")
    val receiptData: ReceiptDto,
    @SerialName("items_data")
    val itemsData: List<ReceiptItemDto>? = null
)
