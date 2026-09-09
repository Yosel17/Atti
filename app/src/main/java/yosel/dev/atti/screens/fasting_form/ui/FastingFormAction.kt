package yosel.dev.atti.screens.fasting_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

sealed class FastingFormAction {
    object TryCatalogsAgain : FastingFormAction()
    object SaveFasting : FastingFormAction()
    data class ToggleSaveFastingDialog(val show: Boolean) : FastingFormAction()

    // Food Sheet
    object OnDismissFoodSheet : FastingFormAction()
    object OnShowFoodSheet : FastingFormAction()
    data class OnSearchFoodQueryChange(val query: String) : FastingFormAction()
    data class OnSelectFood(val catalog: AppCatalogModel) : FastingFormAction()

    // Water Sheet
    object OnDismissWaterSheet : FastingFormAction()
    object OnShowWaterSheet : FastingFormAction()
    data class OnSearchWaterQueryChange(val query: String) : FastingFormAction()
    data class OnSelectWater(val catalog: AppCatalogModel) : FastingFormAction()

    // Add Catalog
    data class OnShowAddCatalogDialog(val catalogTypeId: Int, val catalogTypeName: String) : FastingFormAction()
    object OnDismissAddCatalogDialog : FastingFormAction()
    data class OnSaveAppCatalog(val name: String) : FastingFormAction()
}