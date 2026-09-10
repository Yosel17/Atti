package yosel.dev.atti.screens.asa_classification_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

data class AsaClassificationFormInputsState(
    val selectedAsaClassifications: List<AppCatalogModel> = emptyList(),
    val newTagName: String = ""
) {
    val isValid: Boolean
        get() = selectedAsaClassifications.isNotEmpty()

    fun hasChangesFrom(initial: AsaClassificationFormInputsState): Boolean {
        val currentIds = selectedAsaClassifications.map { it.id }.toSet()
        val initialIds = initial.selectedAsaClassifications.map { it.id }.toSet()
        return currentIds != initialIds
    }
}
