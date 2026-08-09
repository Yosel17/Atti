package yosel.dev.atti.screens.detail_patient.ui

sealed interface DetailPatientEvent {

    data class ShowErrorSnackbar(val message: String) : DetailPatientEvent

}