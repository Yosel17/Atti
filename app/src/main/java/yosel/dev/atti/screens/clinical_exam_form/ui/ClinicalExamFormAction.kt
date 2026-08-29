package yosel.dev.atti.screens.clinical_exam_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

sealed interface ClinicalExamFormAction {
    data object TryCatalogsAgain : ClinicalExamFormAction
    data object SaveClinicalExam : ClinicalExamFormAction
    data class ToggleSaveExamDialog(val show: Boolean) : ClinicalExamFormAction

    // Mucosas
    data class OnMucousMembranesChange(val value: String) : ClinicalExamFormAction

    // Nódulos linfáticos
    data class OnLymphNodesStatusChange(val isInfarted: Boolean) : ClinicalExamFormAction
    data object OnOpenLymphNodesSheet : ClinicalExamFormAction
    data object OnDismissLymphNodesSheet : ClinicalExamFormAction
    data class OnSearchLymphNodesQueryChange(val query: String) : ClinicalExamFormAction
    data class OnToggleLymphNodeOption(val catalog: AppCatalogModel) : ClinicalExamFormAction
    data class OnRemoveLymphNodeOption(val catalog: AppCatalogModel) : ClinicalExamFormAction

    // Pelaje
    data object OnOpenCoatSheet : ClinicalExamFormAction
    data object OnDismissCoatSheet : ClinicalExamFormAction
    data class OnSearchCoatQueryChange(val query: String) : ClinicalExamFormAction
    data class OnSelectCoat(val coat: AppCatalogModel) : ClinicalExamFormAction

    // Palpación abdominal
    data class OnAbdominalPalpationChange(val value: String) : ClinicalExamFormAction

    // Condición corporal
    data class OnBodyConditionChange(val condition: Int) : ClinicalExamFormAction

    // Otros hallazgos
    data class OnOtherFindingsChange(val value: String) : ClinicalExamFormAction

    // Catálogos generales
    data class OnShowAddCatalogDialog(val catalogTypeId: Int, val catalogTypeName: String) : ClinicalExamFormAction
    data object OnDismissAddCatalogDialog : ClinicalExamFormAction
    data class OnSaveAppCatalog(val name: String) : ClinicalExamFormAction
}