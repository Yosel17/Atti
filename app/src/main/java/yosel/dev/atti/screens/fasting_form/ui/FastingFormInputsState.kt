package yosel.dev.atti.screens.fasting_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

data class FastingFormInputsState(
    val selectedFood: AppCatalogModel? = null,
    val selectedWater: AppCatalogModel? = null
) {
    val isValid: Boolean
        get() = selectedFood != null && selectedWater != null

    fun hasChangesFrom(initialState: FastingFormInputsState): Boolean {
        return this.selectedFood?.id != initialState.selectedFood?.id ||
               this.selectedWater?.id != initialState.selectedWater?.id
    }
}