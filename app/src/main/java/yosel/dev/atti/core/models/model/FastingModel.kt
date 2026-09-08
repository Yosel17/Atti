package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class FastingModel(
    val id: String = "",
    val consultationId: String = "",
    val foodFastingCatalogId: Int = 0,
    val waterFastingCatalogId: Int = 0,
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)
}
