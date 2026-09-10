package yosel.dev.atti.screens.asa_classification_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

sealed interface AsaClassificationFormAction {
    data object TryCatalogsAgain : AsaClassificationFormAction
    data object SaveAsaClassification : AsaClassificationFormAction
    data class ToggleSaveAsaClassificationDialog(val show: Boolean) : AsaClassificationFormAction
    data class OnSearchQueryChange(val query: String) : AsaClassificationFormAction
    data class OnNewTagNameChange(val value: String) : AsaClassificationFormAction
    data object OnAddNewTag : AsaClassificationFormAction
    data class OnToggleAsaClassificationOption(val catalog: AppCatalogModel) : AsaClassificationFormAction
    data class OnRemoveAsaClassificationOption(val catalog: AppCatalogModel) : AsaClassificationFormAction
}
