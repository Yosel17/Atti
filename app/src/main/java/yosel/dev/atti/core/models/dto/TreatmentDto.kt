package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TreatmentDto(
    @SerialName("id") val id: String? = null,
    @SerialName("consultation_id") val consultationId: String,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("service_id") val serviceId: String? = null,
    @SerialName("quantity") val quantity: Double = 1.0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int = 1,
    // Relaciones opcionales desde Supabase (Join)
    @SerialName("product") val product: ProductDto? = null,
    @SerialName("service") val service: ServiceDto? = null
)
