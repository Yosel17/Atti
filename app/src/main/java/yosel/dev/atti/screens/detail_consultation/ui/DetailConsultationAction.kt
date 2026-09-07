package yosel.dev.atti.screens.detail_consultation.ui

sealed interface DetailConsultationAction {
    data class ToggleConfirmFinalizeDialog(val show: Boolean) : DetailConsultationAction
    data object FinalizeConsultation : DetailConsultationAction
}
