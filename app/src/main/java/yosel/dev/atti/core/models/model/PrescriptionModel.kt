package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class PrescriptionModel(
    val id: String = "",
    val consultationId: String = "",
    val generalNotes: String = "",
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)
}
