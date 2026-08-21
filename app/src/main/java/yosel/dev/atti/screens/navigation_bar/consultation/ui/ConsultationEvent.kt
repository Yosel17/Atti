package yosel.dev.atti.screens.navigation_bar.consultation.ui

sealed interface ConsultationEvent {
    data class ShowSnackBarError(val message: String) : ConsultationEvent
    data class ShowSnackBarSuccess(val message: String) : ConsultationEvent
}