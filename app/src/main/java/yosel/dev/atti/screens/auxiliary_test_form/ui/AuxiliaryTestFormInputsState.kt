package yosel.dev.atti.screens.auxiliary_test_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

data class AuxiliaryTestFormInputsState(
    val selectedAuxiliaryTests: List<AppCatalogModel> = emptyList(),
    val newTagName: String = ""
) {
    val isValid: Boolean
        get() = selectedAuxiliaryTests.isNotEmpty()

    fun hasChangesFrom(initial: AuxiliaryTestFormInputsState): Boolean {
        val currentIds = selectedAuxiliaryTests.map { it.id }.toSet()
        val initialIds = initial.selectedAuxiliaryTests.map { it.id }.toSet()
        return currentIds != initialIds
    }
}
