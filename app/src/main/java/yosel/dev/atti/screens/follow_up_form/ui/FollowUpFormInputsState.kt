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
    val reason: String = "",
    val patientName: String = "",
    val clientName: String = "",
    val clientPhone: String = "+502 ",
    val touchedFields: Set<Int> = emptySet()
) {
    companion object {
        const val FIELD_PATIENT_NAME = 1
        const val FIELD_CLIENT_NAME = 2
        const val FIELD_CLIENT_PHONE = 3
    }

    // Guarda la fecha y hora junto con el offset horario local (ej. 2026-09-06T09:00:00-06:00)
    val scheduledAtIso: String
        get() {
            val localTimeClean = selectedTime.truncatedTo(ChronoUnit.MINUTES)
            val zonedDateTime = ZonedDateTime.of(selectedDate, localTimeClean, ZoneId.systemDefault())
            return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }

    private val isPhoneValid: Boolean
        get() {
            val digitsOnly = clientPhone.filter { it.isDigit() }
            return clientPhone.isNotBlank() && digitsOnly.length >= 4
        }

    val isStandaloneValid: Boolean
        get() = patientName.isNotBlank() &&
                clientName.isNotBlank() &&
                isPhoneValid

    fun isError(field: Int): Boolean {
        if (field !in touchedFields) return false
        return when (field) {
            FIELD_PATIENT_NAME -> patientName.isBlank()
            FIELD_CLIENT_NAME -> clientName.isBlank()
            FIELD_CLIENT_PHONE -> !isPhoneValid
            else -> false
        }
    }

    fun hasChangesFrom(initial: FollowUpFormInputsState, isStandalone: Boolean = false): Boolean {
        val baseChanges = selectedDate != initial.selectedDate ||
                selectedTime.truncatedTo(ChronoUnit.MINUTES) != initial.selectedTime.truncatedTo(ChronoUnit.MINUTES) ||
                reason.trim() != initial.reason.trim()

        return if (isStandalone) {
            baseChanges ||
                    patientName.trim() != initial.patientName.trim() ||
                    clientName.trim() != initial.clientName.trim() ||
                    clientPhone.trim() != initial.clientPhone.trim()
        } else {
            baseChanges
        }
    }
}