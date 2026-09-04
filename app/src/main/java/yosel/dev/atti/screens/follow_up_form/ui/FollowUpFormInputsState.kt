package yosel.dev.atti.screens.follow_up_form.ui

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class FollowUpFormInputsState(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime = LocalTime.of(8, 0),
    val isCustomDateFromPicker: Boolean = false,
    val reason: String = ""
) {
    val scheduledAtIso: String
        get() = LocalDateTime.of(selectedDate, selectedTime).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    val isValid: Boolean
        get() = true

    fun hasChangesFrom(initial: FollowUpFormInputsState): Boolean {
        return selectedDate != initial.selectedDate ||
                selectedTime != initial.selectedTime ||
                reason.trim() != initial.reason.trim()
    }
}
