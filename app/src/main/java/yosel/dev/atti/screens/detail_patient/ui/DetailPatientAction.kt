package yosel.dev.atti.screens.detail_patient.ui

sealed interface DetailPatientAction {

    data object OnEditClick: DetailPatientAction

    data class ToggleShowDialogConfirmDelete(val show: Boolean): DetailPatientAction

    data object DeletePatient: DetailPatientAction

    data class ToggleShowDialogConfirmRestore(val show: Boolean): DetailPatientAction

    data object RestorePatient: DetailPatientAction
}