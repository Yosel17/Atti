package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class AppCatalogModel(
    val id: Int = 0,
    val catalogTypeId: Int = 0,
    val name: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val createdAt: String = ""
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)
}
