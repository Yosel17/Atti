package yosel.dev.atti.screens.top_level.patients.ui

sealed interface PatientsEvent {
    data class ShowSnackBarError(val message: String) : PatientsEvent
}