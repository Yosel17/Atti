package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate
import yosel.dev.atti.core.utils.formatScheduledDate
import yosel.dev.atti.core.utils.formatScheduledDayOfWeek
import yosel.dev.atti.core.utils.formatScheduledTime

data class FollowUpModel(
    val id: String = "",
    val consultationId: String = "",
    val patientId: String = "",
    val scheduledAt: String = "",
    val reason: String = "",
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)

    // Formato para el diseño: "Miércoles, 16 de Octubre"
    val formattedScheduledDate: String
        get() = formatScheduledDate(isoString = scheduledAt)

    // Formato para la selección: "10:00 AM"
    val formattedScheduledTime: String
        get() = formatScheduledTime(isoString = scheduledAt)

    // Formato corto para las tarjetas del calendario: "Mié 16"
    val formattedDayChip: String
        get() = formatScheduledDayOfWeek(isoString = scheduledAt)
}
