package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("id") val id: String? = null,
    @SerialName("supplier_id") val supplierId: String? = null,
    @SerialName("category_id") val categoryId: Int? = null,
    @SerialName("unit_type_id") val unitTypeId: Int? = null,
    @SerialName("commercial_name") val commercialName: String,
    @SerialName("brand") val brand: String? = null,
    @SerialName("purchase_price") val purchasePrice: Double = 0.0,
    @SerialName("sale_price") val salePrice: Double = 0.0,
    @SerialName("stock") val stock: Double = 0.0,
    @SerialName("min_stock") val minStock: Double? = 0.0,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int,

    // Relaciones mapeadas desde Supabase (Opcionales)
    @SerialName("supplier") val supplier: SupplierDto? = null,
    @SerialName("category") val category: AppCatalogDto? = null,
    @SerialName("unit_type") val unitType: AppCatalogDto? = null
)
