package yosel.dev.atti.core.models.dto

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ReceiptItemDto(
    @SerialName("id") val id: String? = null,
    @SerialName("receipt_id") val receiptId: String? = null,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("service_id") val serviceId: String? = null,
    @EncodeDefault @SerialName("quantity") val quantity: Double = 1.0,
    @EncodeDefault @SerialName("unit_price") val unitPrice: Double = 0.0,
    @EncodeDefault @SerialName("subtotal") val subtotal: Double = 0.0,
    @SerialName("created_at") val createdAt: String? = null,
    @EncodeDefault @SerialName("status") val status: Int = 1,

    // Relaciones opcionales desde Supabase (Joins)
    @SerialName("product") val product: ProductDto? = null,
    @SerialName("service") val service: ServiceDto? = null
)
