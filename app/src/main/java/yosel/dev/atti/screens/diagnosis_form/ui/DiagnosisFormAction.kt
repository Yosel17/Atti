package yosel.dev.atti.screens.diagnosis_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

sealed interface DiagnosisFormAction {
    data object TryCatalogsAgain : DiagnosisFormAction
    data object SaveDiagnosis : DiagnosisFormAction
    data class ToggleSaveDiagnosisDialog(val show: Boolean) : DiagnosisFormAction
    data class OnSearchQueryChange(val query: String) : DiagnosisFormAction
    data class OnNewTagNameChange(val value: String) : DiagnosisFormAction
    data object OnAddNewTag : DiagnosisFormAction
    data class OnToggleDiagnosisOption(val catalog: AppCatalogModel) : DiagnosisFormAction
    data class OnRemoveDiagnosisOption(val catalog: AppCatalogModel) : DiagnosisFormAction
}