package yosel.dev.atti.screens.detail_consultation.ui

sealed interface DetailConsultationEvent {

    data class ShowErrorSnackbar(val message: String) : DetailConsultationEvent
}