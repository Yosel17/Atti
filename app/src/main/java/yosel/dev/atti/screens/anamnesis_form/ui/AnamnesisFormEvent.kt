package yosel.dev.atti.screens.anamnesis_form.ui

sealed interface AnamnesisFormEvent {

    data class ShowErrorSnackbar(val message: String) : AnamnesisFormEvent

    data class ShowSuccessSnackbar(val message: String) : AnamnesisFormEvent

    data class ShowToast(val message: String) : AnamnesisFormEvent
}