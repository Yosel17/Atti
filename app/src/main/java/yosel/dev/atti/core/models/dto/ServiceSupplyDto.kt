package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceSupplyDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("service_id") val serviceId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("quantity_required") val quantityRequired: Double = 1.0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int = 1,
    // Relaciones opcionales desde Supabase
    @SerialName("product") val product: ProductDto? = null,
    @SerialName("service") val service: ServiceDto? = null
)
