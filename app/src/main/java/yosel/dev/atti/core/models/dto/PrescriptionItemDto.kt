package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrescriptionItemDto(
    @SerialName("id") val id: String? = null,
    @SerialName("prescription_id") val prescriptionId: String,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("custom_product_name") val customProductName: String? = null,
    @SerialName("instructions") val instructions: String,
    @SerialName("quantity") val quantity: Double = 1.0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int = 1,
    // Relación opcional para cuando el producto proviene del inventario
    @SerialName("product") val product: ProductDto? = null
)
