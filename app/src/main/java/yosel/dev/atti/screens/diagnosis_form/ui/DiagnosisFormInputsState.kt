package yosel.dev.atti.screens.diagnosis_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

data class DiagnosisFormInputsState(
    val selectedDiagnoses: List<AppCatalogModel> = emptyList(),
    val newTagName: String = ""
) {
    val isValid: Boolean
        get() = selectedDiagnoses.isNotEmpty()

    fun hasChangesFrom(initial: DiagnosisFormInputsState): Boolean {
        val currentIds = selectedDiagnoses.map { it.id }.toSet()
        val initialIds = initial.selectedDiagnoses.map { it.id }.toSet()
        return currentIds != initialIds
    }
}
