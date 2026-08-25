package yosel.dev.atti.screens.anamnesis_form.ui

sealed interface AnamnesisFormAction {

    data object TryCatalogsAgain: AnamnesisFormAction
}