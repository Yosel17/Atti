package yosel.dev.atti.screens.detail_service.ui

import yosel.dev.atti.core.navigation.main.Screens

sealed interface DetailServiceAction {
    data object OnEditClick : DetailServiceAction
    data class ToggleShowDialogConfirmDelete(val show: Boolean) : DetailServiceAction
    data object DeleteService : DetailServiceAction
    data class ToggleShowDialogConfirmRestore(val show: Boolean) : DetailServiceAction
    data object RestoreService : DetailServiceAction
    data class ToggleShowDialogInformation(val show: Boolean) : DetailServiceAction
    data class OnNavigationMain(val screen: Screens) : DetailServiceAction
}