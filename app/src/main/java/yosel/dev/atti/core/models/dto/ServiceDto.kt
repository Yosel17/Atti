package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceDto(
    @SerialName("id") val id: String? = null,
    @SerialName("category_id") val categoryId: Int? = null,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("sale_price") val salePrice: Double = 0.0,
    @SerialName("estimated_cost") val estimatedCost: Double? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int,

    // Relaciones mapeadas desde Supabase (Opcionales)
    @SerialName("category") val category: AppCatalogDto? = null,
    @SerialName("supplies") val supplies: List<ServiceSupplyDto> = emptyList()
)
