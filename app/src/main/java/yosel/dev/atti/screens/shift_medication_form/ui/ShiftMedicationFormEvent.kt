package yosel.dev.atti.screens.shift_medication_form.ui

sealed interface ShiftMedicationFormEvent {
    data class ShowErrorSnackbar(val message: String) : ShiftMedicationFormEvent
    data class ShowSuccessSnackbar(val message: String) : ShiftMedicationFormEvent
    data class ShowToast(val message: String) : ShiftMedicationFormEvent
}
