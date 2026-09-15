package yosel.dev.atti.screens.detail_patient.ui

sealed interface DetailPatientAction {

    data object OnEditClick: DetailPatientAction

    data class ToggleShowDialogConfirmDelete(val show: Boolean): DetailPatientAction

    data object DeletePatient: DetailPatientAction

    data class ToggleShowDialogConfirmRestore(val show: Boolean): DetailPatientAction

    data object RestorePatient: DetailPatientAction

    data class OnConsultationClick(val consultationId: String, val consultationTypeId: Int): DetailPatientAction
}
