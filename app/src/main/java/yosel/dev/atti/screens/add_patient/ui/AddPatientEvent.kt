package yosel.dev.atti.screens.add_patient.ui

sealed interface AddPatientEvent {

    data class ShowErrorSnackbar(val message: String) : AddPatientEvent

    data class ShowSuccessSnackbar(val message: String) : AddPatientEvent
}