package yosel.dev.atti.screens.detail_client.ui

import yosel.dev.atti.core.navigation.main.Screens

sealed interface DetailClientEvent {

    data class ShowErrorSnackbar(val message: String) : DetailClientEvent
    data class ShowSuccessSnackbar(val message: String) : DetailClientEvent

    data class OnCallClick(val phoneNumber: String) : DetailClientEvent
    data class OnWhatsappClick(val phoneNumber: String) : DetailClientEvent

    data class OnNavigationMain(val screen: Screens): DetailClientEvent
}