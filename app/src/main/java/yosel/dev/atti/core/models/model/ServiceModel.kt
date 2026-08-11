package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class ServiceModel(
    val id: String = "",
    val categoryId: Int = 0,
    val name: String = "",
    val description: String = "",
    val salePrice: Double = 0.0,
    val estimatedCost: Double = 0.0,
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)
}
