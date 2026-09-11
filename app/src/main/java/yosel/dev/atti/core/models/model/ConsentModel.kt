package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class ConsentModel(
    val id: String = "",
    val consultationId: String = "",
    val imageUrl: String = "",
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)
}
