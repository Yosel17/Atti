package yosel.dev.atti.screens.top_level.consultation.ui

sealed interface ConsultationEvent {
    data class ShowSnackBarError(val message: String) : ConsultationEvent
    data class ShowSnackBarSuccess(val message: String) : ConsultationEvent
}