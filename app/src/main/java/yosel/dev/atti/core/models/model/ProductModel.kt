package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class ProductModel(
    val id: String = "",
    val supplierId: String? = null,
    val categoryId: Int = 0,
    val unitTypeId: Int = 0,
    val commercialName: String = "",
    val brand: String = "",
    val purchasePrice: Double = 0.0,
    val salePrice: Double = 0.0,
    val stock: Int = 0,
    val minStock: Int = 0,
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)

    val isLowStock: Boolean
        get() = stock <= minStock
}
