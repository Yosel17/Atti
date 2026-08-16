package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class ServiceSupplyModel(
    val id: Int = 0,
    val serviceId: String = "",
    val productId: String = "",
    val quantityRequired: Double = 1.0,
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)
}
