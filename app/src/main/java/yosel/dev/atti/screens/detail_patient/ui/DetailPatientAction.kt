package yosel.dev.atti.screens.detail_patient.ui

sealed interface DetailPatientAction {

    data object OnEditClick: DetailPatientAction

    data object OnDeleteClick: DetailPatientAction
}