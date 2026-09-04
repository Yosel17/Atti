package yosel.dev.atti.screens.observation_form.ui

sealed interface ObservationFormAction {
    data object TryLoadAgain : ObservationFormAction
    data class OnObservationChange(val text: String) : ObservationFormAction
    data object SaveObservation : ObservationFormAction
    data class ToggleSaveDialog(val show: Boolean) : ObservationFormAction
}