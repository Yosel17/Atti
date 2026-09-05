package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReceiptDto(
    @SerialName("id") val id: String? = null,
    @SerialName("receipt_number") val receiptNumber: Long? = null,
    @SerialName("consultation_id") val consultationId: String? = null,
    @SerialName("customer_name") val customerName: String? = null,
    @SerialName("subtotal") val subtotal: Double = 0.0,
    @SerialName("discount") val discount: Double = 0.0,
    @SerialName("tax") val tax: Double = 0.0,
    @SerialName("total") val total: Double = 0.0,
    @SerialName("notes") val notes: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int = 1,

    // Relaciones opcionales desde Supabase (Joins)
    @SerialName("consultation") val consultation: ConsultationDto? = null,
    @SerialName("receipt_items") val items: List<ReceiptItemDto> = emptyList()
)
