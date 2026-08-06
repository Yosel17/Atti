package yosel.dev.atti.core.utils

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CrueltyFree
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.net.toUri
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import yosel.dev.atti.R
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDate(isoString: String): String {
    if (isoString.isBlank()) return ""
    return try {
        // Supabase envía el formato "yyyy-MM-dd HH:mm:ss.SSSSSS+00"
        // Instant.parse espera el formato ISO con 'T' en lugar de espacio.
        val sanitizedIso = isoString.replace(" ", "T")
        val instant = Instant.parse(sanitizedIso)
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

data class SpeciesInfo(
    val label: String,
    val icon: Int
)

fun getSpeciesInfo(speciesId: Int): SpeciesInfo {
    return when (speciesId) {
        1 -> SpeciesInfo("Canino", R.drawable.ic_canine)
        2 -> SpeciesInfo("Felino", R.drawable.ic_feline)// Puedes usar Pets o CrueltyFree según prefieras
        3 -> SpeciesInfo("Silvestre", R.drawable.ic_wild)
        else -> SpeciesInfo("Otro", R.drawable.ic_animals)
    }
}

data class GenderInfo(
    val label: String,
    val icon: ImageVector
)

fun getGenderInfo(genderId: Int): GenderInfo {
    return when (genderId) {
        1 -> GenderInfo("Macho", Icons.Outlined.Male)
        2 -> GenderInfo("Hembra", Icons.Outlined.Female)
        else -> GenderInfo("Desconocido", Icons.Outlined.QuestionMark)
    }
}
