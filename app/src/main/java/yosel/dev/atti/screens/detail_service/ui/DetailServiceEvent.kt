package yosel.dev.atti.screens.detail_service.ui

import yosel.dev.atti.core.navigation.main.Screens

sealed interface DetailServiceEvent {
    data class ShowErrorSnackbar(val message: String) : DetailServiceEvent
    data class ShowSuccessSnackbar(val message: String) : DetailServiceEvent
    data class OnNavigationMain(val screen: Screens) : DetailServiceEvent
}