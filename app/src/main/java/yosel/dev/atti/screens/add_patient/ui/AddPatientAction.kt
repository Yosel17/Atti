package yosel.dev.atti.screens.add_patient.ui

sealed interface AddPatientAction {

    data object RegisterPatient : AddPatientAction
}