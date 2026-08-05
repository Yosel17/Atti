package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class PatientModel(
    val id: String = "",
    val clientId: String = "",
    val name: String = "",
    val speciesId: Int = 0,
    val genderId: Int = 0,
    val breed: String = "",
    val ageYears: Int = 0,
    val ageMonths: Int = 0,
    val color: String = "",
    val isNeutered: Boolean = false,
    val photoUrl: String = "",
    val createdAt: String = ""
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)

    val formattedAge: String
        get() = when {
            ageYears > 0 && ageMonths > 0 -> "$ageYears años y $ageMonths meses"
            ageYears > 0 -> "$ageYears años"
            ageMonths > 0 -> "$ageMonths meses"
            else -> "Recién nacido / Sin información"
        }
}
