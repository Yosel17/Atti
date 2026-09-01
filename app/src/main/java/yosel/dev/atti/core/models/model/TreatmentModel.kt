package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class TreatmentModel(
    val id: String = "",
    val consultationId: String = "",
    val productId: String? = null,
    val serviceId: String? = null,
    val quantity: Double = 1.0,
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)

    val isProduct: Boolean
        get() = !productId.isNullOrBlank()

    val isService: Boolean
        get() = !serviceId.isNullOrBlank()
}
