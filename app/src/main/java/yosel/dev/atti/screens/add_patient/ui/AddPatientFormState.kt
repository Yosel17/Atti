package yosel.dev.atti.screens.add_patient.ui

import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.utils.Constants

data class AddPatientFormState(
    val name: String = "",
    val speciesId: Int = 0,
    val breed: String = "",
    val genderId: Int = 0,
    val ageYears: String = "",
    val ageMonths: String = "",
    val color: String = "",
    val isNeutered: Boolean = false,
    val selectedClient: ClientModel? = null,
    val touchedFields: Set<Int> = emptySet()
) {
    val isValid: Boolean
        get() = name.isNotBlank() &&
                speciesId != 0 &&
                breed.isNotBlank() &&
                genderId != 0 &&
                ageYears.isNotBlank() &&
                ageMonths.isNotBlank() &&
                color.isNotBlank() &&
                selectedClient != null

    fun isError(field: Int): Boolean {
        if (field !in touchedFields) return false
        return when (field) {
            Constants.PATIENT_NAME_FIELD -> name.isBlank()
            Constants.PATIENT_BREED_FIELD -> breed.isBlank()
            Constants.PATIENT_AGE_YEARS_FIELD -> ageYears.isBlank()
            Constants.PATIENT_AGE_MONTHS_FIELD -> ageMonths.isBlank()
            Constants.PATIENT_COLOR_FIELD -> color.isBlank()
            else -> false
        }
    }
}
