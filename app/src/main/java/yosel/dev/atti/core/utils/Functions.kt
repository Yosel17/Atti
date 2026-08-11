package yosel.dev.atti.core.utils

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.AddCircleOutline
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
fun getIconSpecies(speciesId: Int): Int {
    return when (speciesId) {
        0 -> R.drawable.ic_circule_add
        Constants.CANINE_SPECIES_CATALOG -> R.drawable.ic_canine
        Constants.FELINE_SPECIES_CATALOG -> R.drawable.ic_feline
        Constants.WILD_SPECIES_CATALOG -> R.drawable.ic_wild
        else -> R.drawable.ic_animals
    }
}

fun getIconGender(genderId: Int): ImageVector {
    return when (genderId) {
        0 -> Icons.Outlined.AddCircleOutline
        Constants.MALE_GENDER_CATALOG -> Icons.Outlined.Male
        Constants.FEMALE_GENDER_CATALOG -> Icons.Outlined.Female
        else -> Icons.Outlined.QuestionMark
    }
}
