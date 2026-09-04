package yosel.dev.atti.core.utils

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Emergency
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.MonitorHeart
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
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Actualización de formatDate general para prescindir del Instant deprecado.
 * Ejemplo: "16 de octubre, 2026"
 */
fun formatDate(isoString: String): String {
    val dateTime = parseToLocalDateTime(isoString) ?: return isoString
    val locale = Locale.forLanguageTag("es-ES")
    val formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", locale)
    return dateTime.format(formatter)
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

fun getConsultationStepScreen(
    stepName: String,
    consultationId: String,
    recordId: String? = null
): Screens {
    return when (stepName.normalize()) {
        "anamnesis" -> Screens.AnamnesisForm(
            consultationId = consultationId,
            anamnesisId = recordId
        )
        "examen clinico" -> Screens.ClinicalExamForm(
            consultationId = consultationId,
            examId = recordId
        )
        "constantes fisiologicas" -> Screens.PhysioConstsForm(
            consultationId = consultationId,
            constsId = recordId
        )
        "diagnostico" -> Screens.DiagnosisForm(
            consultationId = consultationId,
            diagnosisId = recordId
        )
        "pruebas auxiliares" -> Screens.Empty
        "tratamiento" -> Screens.TreatmentForm(
            consultationId = consultationId,
            treatmentId = recordId
        )
        "receta" -> Screens.PrescriptionForm(
            consultationId = consultationId,
            prescriptionId = recordId
        )
        "observaciones" -> Screens.ObservationForm(
            consultationId = consultationId,
            observationId = recordId
        )
        "reconsulta" -> Screens.Empty
        else -> Screens.Empty
    }
}

fun getConsultationStepIcon(stepName: String): ImageVector {
    return when (stepName.normalize()) {
        "anamnesis" -> Icons.Default.HistoryEdu
        "examen clinico" -> Icons.Default.MedicalInformation
        "constantes fisiologicas" -> Icons.Default.MonitorHeart
        "diagnostico" -> Icons.AutoMirrored.Filled.Assignment
        "pruebas auxiliares" -> Icons.Default.Biotech
        "tratamiento" -> Icons.Default.Medication
        "receta" -> Icons.AutoMirrored.Filled.ReceiptLong
        "observaciones" -> Icons.Default.Visibility
        "reconsulta" -> Icons.Default.EventRepeat
        else -> Icons.AutoMirrored.Filled.HelpOutline
    }
}

fun getFormattedCurrentDate(): String {
    val localeSpanish = Locale.forLanguageTag("es-ES")
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", localeSpanish)
    return LocalDate.now().format(formatter)
}

/**
 * Parsea con seguridad cualquier formato ISO devuelto por Supabase o SQLite
 * manejando microsegundos, espacios (" ") o "T", y offsets como "+00" o "+00:00".
 */
private fun parseToLocalDateTime(isoString: String): LocalDateTime? {
    if (isoString.isBlank()) return null
    return try {
        var sanitized = isoString.trim().replace(" ", "T")

        // Supabase/Postgres a veces envía offset de 2 dígitos (+00); java.time requiere +00:00
        if (Regex("[+-]\\d{2}$").containsMatchIn(sanitized)) {
            sanitized += ":00"
        }

        if (sanitized.contains("+") || sanitized.endsWith("Z") || Regex("-\\d{2}:\\d{2}$").containsMatchIn(sanitized)) {
            OffsetDateTime.parse(sanitized)
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
        } else {
            LocalDateTime.parse(sanitized)
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * Fecha completa para cabeceras o detalles.
 * Ejemplo: "Miércoles, 16 de octubre"
 */
fun formatScheduledDate(isoString: String): String {
    val dateTime = parseToLocalDateTime(isoString) ?: return isoString
    val locale = Locale.forLanguageTag("es-ES")
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", locale)
    return dateTime.format(formatter)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}

/**
 * Hora exacta en formato de 12 horas para los chips de mañana/tarde.
 * Ejemplo: "10:00 AM" o "03:30 PM"
 */
fun formatScheduledTime(isoString: String): String {
    val dateTime = parseToLocalDateTime(isoString) ?: return isoString
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
    return dateTime.format(formatter).uppercase()
}

/**
 * Día abreviado y número para los chips superiores del selector de fecha.
 * Ejemplo: "Mié 16"
 */
fun formatScheduledDayOfWeek(isoString: String): String {
    val dateTime = parseToLocalDateTime(isoString) ?: return isoString
    val locale = Locale.forLanguageTag("es-ES")
    val formatter = DateTimeFormatter.ofPattern("EEE d", locale)
    return dateTime.format(formatter)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}
