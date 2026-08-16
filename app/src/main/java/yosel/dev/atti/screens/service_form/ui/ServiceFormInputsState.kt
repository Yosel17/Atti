package yosel.dev.atti.screens.service_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.utils.Constants

data class ServiceFormInputsState(
    val name: String = "",
    val selectedCategory: AppCatalogModel? = null,
    val salePrice: String = "",
    val touchedFields: Set<Int> = emptySet()
) {

    val isValid: Boolean
        get() = name.isNotBlank() &&
                selectedCategory != null &&
                salePrice.isNotBlank()

    fun isError(field: Int): Boolean {
        if (field !in touchedFields) return false
        return when (field) {
            Constants.SERVICE_NAME_FIELD -> name.isBlank()
            Constants.SERVICE_CATEGORY_FIELD -> selectedCategory == null
            Constants.SERVICE_SALE_PRICE_FIELD -> salePrice.isBlank()
            else -> false
        }
    }

    fun hasChangesFrom(initial: ServiceFormInputsState): Boolean {
        return name != initial.name ||
                selectedCategory?.id != initial.selectedCategory?.id ||
                salePrice != initial.salePrice
    }
}
