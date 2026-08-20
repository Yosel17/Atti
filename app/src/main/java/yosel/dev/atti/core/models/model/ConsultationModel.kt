package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class ConsultationModel(
    val id: String = "",
    val patientId: String = "",
    val consultationTypeId: Int = 0,
    val startedAt: String = "",
    val completedAt: String = "",
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)

    val formattedStartedAt: String
        get() = formatDate(isoString = startedAt)

    val formattedCompletedAt: String
        get() = formatDate(isoString = completedAt)
}
