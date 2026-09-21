package yosel.dev.atti.screens.top_level.consultations.ui

sealed interface ConsultationsEvent {
    data class ShowSnackBarError(val message: String) : ConsultationsEvent
}