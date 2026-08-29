package yosel.dev.atti.screens.physio_consts_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

sealed interface PhysioConstsFormAction {
    data object TryCatalogsAgain : PhysioConstsFormAction
    data object SaveConstants : PhysioConstsFormAction
    data class ToggleSaveDialog(val show: Boolean) : PhysioConstsFormAction

    // Inputs
    data class OnTemperatureChange(val value: String) : PhysioConstsFormAction
    data class OnHeartRateChange(val value: String) : PhysioConstsFormAction
    data class OnRespiratoryRateChange(val value: String) : PhysioConstsFormAction
    data class OnWeightChange(val value: String) : PhysioConstsFormAction
    data class OnCapillaryRefillTimeChange(val value: Int) : PhysioConstsFormAction
    data class OnSkinTurgorChange(val value: Int) : PhysioConstsFormAction

    // Unidad de Peso
    data object OnOpenWeightUnitSheet : PhysioConstsFormAction
    data object OnDismissWeightUnitSheet : PhysioConstsFormAction
    data class OnSearchWeightUnitQueryChange(val query: String) : PhysioConstsFormAction
    data class OnSelectWeightUnit(val unit: AppCatalogModel) : PhysioConstsFormAction

    // Diálogo nuevo catálogo
    data class OnShowAddCatalogDialog(val catalogTypeId: Int, val catalogTypeName: String) : PhysioConstsFormAction
    data object OnDismissAddCatalogDialog : PhysioConstsFormAction
    data class OnSaveAppCatalog(val name: String) : PhysioConstsFormAction
}