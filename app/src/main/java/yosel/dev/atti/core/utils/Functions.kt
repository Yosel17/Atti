package yosel.dev.atti.core.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Instant

fun formatDate(isoString: String): String {
    if (isoString.isBlank()) return ""
    return try {
        val instant = Instant.parse(isoString)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val javaLocalDateTime = java.time.LocalDateTime.of(
            localDateTime.year,
            localDateTime.month.number,
            localDateTime.day,
            localDateTime.hour,
            localDateTime.minute
        )

        // FORMA MODERNA (remueve el deprecado):
        val locale = Locale.forLanguageTag("es-ES")
        val formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", locale)

        javaLocalDateTime.format(formatter)
    } catch (e: Exception) {
        isoString
    }
}