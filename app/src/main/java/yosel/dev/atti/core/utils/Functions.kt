package yosel.dev.atti.core.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
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

/**
 * Abre el marcador telefónico con el número proporcionado.
 * @return true si se pudo iniciar la actividad, false de lo contrario.
 */
fun Context.dialPhoneNumber(phoneNumber: String): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_DIAL, "tel:$phoneNumber".toUri())
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * Abre la aplicación de WhatsApp con un mensaje directo al número proporcionado.
 * @return true si se pudo iniciar la actividad, false de lo contrario (ej. WhatsApp no instalado).
 */
fun Context.openWhatsApp(phoneNumber: String): Boolean {
    return try {
        val cleanNumber = phoneNumber.filter { it.isDigit() }
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "https://wa.me/$cleanNumber".toUri()
            setPackage("com.whatsapp")
        }
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
