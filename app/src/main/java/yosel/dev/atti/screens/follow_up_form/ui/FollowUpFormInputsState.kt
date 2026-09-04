package yosel.dev.atti.screens.follow_up_form.ui

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class FollowUpFormInputsState(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime = LocalTime.of(8, 0),
    val isCustomDateFromPicker: Boolean = false,
    val reason: String = ""
) {
    // Guarda la fecha y hora junto con el offset horario local (ej. 2026-09-06T09:00:00-06:00)
    val scheduledAtIso: String
        get() {
            val localTimeClean = selectedTime.truncatedTo(ChronoUnit.MINUTES)
            val zonedDateTime = ZonedDateTime.of(selectedDate, localTimeClean, ZoneId.systemDefault())
            return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }

    val isValid: Boolean
        get() = true

    fun hasChangesFrom(initial: FollowUpFormInputsState): Boolean {
        return selectedDate != initial.selectedDate ||
                selectedTime.truncatedTo(ChronoUnit.MINUTES) != initial.selectedTime.truncatedTo(ChronoUnit.MINUTES) ||
                reason.trim() != initial.reason.trim()
    }
}
