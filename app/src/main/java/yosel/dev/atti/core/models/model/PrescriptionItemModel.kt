package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class PrescriptionItemModel(
    val id: String = "",
    val prescriptionId: String = "",
    val productId: String? = null,
    val customProductName: String = "",
    val instructions: String = "",
    val quantity: Double = 1.0,
    val createdAt: String = "",
    val status: Int = 1
) {
    val isCustomProduct: Boolean
        get() = productId.isNullOrBlank()

    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)
}
