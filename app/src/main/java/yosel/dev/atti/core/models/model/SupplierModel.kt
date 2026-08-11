package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class SupplierModel(
    val id: String = "",
    val name: String = "",
    val taxId: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)
}
