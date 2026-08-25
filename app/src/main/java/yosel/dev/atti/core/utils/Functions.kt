package yosel.dev.atti.core.utils

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.CrueltyFree
import androidx.compose.material.icons.outlined.Emergency
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.net.toUri
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import yosel.dev.atti.R
import yosel.dev.atti.core.navigation.main.Screens
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.toJavaInstant

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
 * Sanitiza la cadena manteniendo el prefijo '+' y los dígitos.
 */
fun Context.dialPhoneNumber(phoneNumber: String): Boolean {
    return try {
        val sanitizedNumber = phoneNumber.filter { it.isDigit() || it == '+' }
        val intent = Intent(Intent.ACTION_DIAL, "tel:$sanitizedNumber".toUri())
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * Abre la aplicación de WhatsApp con un mensaje directo al número proporcionado.
 * wa.me requiere únicamente números con su código de país (sin '+' ni espacios).
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

fun getIconForConsultationReason(reasonName: String): ImageVector {
    val normalized = reasonName.normalize()
    return when {
        normalized.contains("general") -> Icons.Outlined.MedicalServices
        normalized.contains("control") -> Icons.Outlined.AssignmentTurnedIn
        normalized.contains("profilaxis") || normalized.contains("dental") -> Icons.Outlined.CleaningServices
        normalized.contains("cirugia") || normalized.contains("quirurgic") -> Icons.Outlined.MonitorHeart
        normalized.contains("domicilio") || normalized.contains("casa") -> Icons.Outlined.HomeWork
        normalized.contains("emergencia") || normalized.contains("urgencia") -> Icons.Outlined.Emergency
        normalized.contains("hospital") || normalized.contains("internado") -> Icons.Outlined.LocalHospital
        else -> Icons.Outlined.MedicalServices
    }
}

fun getConsultationStepScreen(stepName: String, consultationId: String): Screens {
    return when (stepName.trim().lowercase()) {
        "anamnesis" -> Screens.AnamnesisForm(consultationId = consultationId)
        "examen clínico", "examen clinico" -> Screens.Empty
        "constantes fisiológicas", "constantes fisiologicas" -> Screens.Empty
        "diagnóstico", "diagnostico" -> Screens.Empty
        "pruebas auxiliares" -> Screens.Empty
        "tratamiento" -> Screens.Empty
        "receta" -> Screens.Empty
        "observaciones" -> Screens.Empty
        "reconsulta" -> Screens.Empty
        else -> Screens.Empty
    }
}

fun getFormattedCurrentDate(): String {
    val localeSpanish = Locale.forLanguageTag("es-ES")
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", localeSpanish)
    return LocalDate.now().format(formatter)
}
