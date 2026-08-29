package yosel.dev.atti.screens.clinical_exam_form.ui

sealed interface ClinicalExamFormEvent {
    data class ShowErrorSnackbar(val message: String) : ClinicalExamFormEvent
    data class ShowSuccessSnackbar(val message: String) : ClinicalExamFormEvent
    data class ShowToast(val message: String) : ClinicalExamFormEvent
}