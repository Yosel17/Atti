package yosel.dev.atti.screens.auxiliary_test_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

sealed interface AuxiliaryTestFormAction {
    data object TryCatalogsAgain : AuxiliaryTestFormAction
    data object SaveAuxiliaryTest : AuxiliaryTestFormAction
    data class ToggleSaveAuxiliaryTestDialog(val show: Boolean) : AuxiliaryTestFormAction
    data class OnSearchQueryChange(val query: String) : AuxiliaryTestFormAction
    data class OnNewTagNameChange(val value: String) : AuxiliaryTestFormAction
    data object OnAddNewTag : AuxiliaryTestFormAction
    data class OnToggleAuxiliaryTestOption(val catalog: AppCatalogModel) : AuxiliaryTestFormAction
    data class OnRemoveAuxiliaryTestOption(val catalog: AppCatalogModel) : AuxiliaryTestFormAction
}
