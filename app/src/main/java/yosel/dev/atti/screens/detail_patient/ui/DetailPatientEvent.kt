package yosel.dev.atti.screens.detail_patient.ui

import yosel.dev.atti.core.navigation.main.Screens

sealed interface DetailPatientEvent {

    data class ShowErrorSnackbar(val message: String) : DetailPatientEvent

    data class OnNavigationMain(val screen: Screens) : DetailPatientEvent

}