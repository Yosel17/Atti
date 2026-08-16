package yosel.dev.atti.screens.service_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductModel
import yosel.dev.atti.core.utils.Constants

enum class ExpenseMode {
    MANUAL,
    LINK_PRODUCTS
}

data class SelectedProductSupply(
    val product: ProductModel,
    val quantity: Double = 1.0
)

data class ServiceFormInputsState(
    val name: String = "",
    val selectedCategory: AppCatalogModel? = null,
    val salePrice: String = "",
    val expenseMode: ExpenseMode = ExpenseMode.MANUAL,
    val estimatedCost: String = "",
    val selectedProducts: List<SelectedProductSupply> = emptyList(),
    val touchedFields: Set<Int> = emptySet()
) {
    val isValid: Boolean
        get() = name.isNotBlank() &&
                selectedCategory != null &&
                salePrice.isNotBlank() &&
                when (expenseMode) {
                    ExpenseMode.MANUAL -> estimatedCost.isNotBlank()
                    ExpenseMode.LINK_PRODUCTS -> selectedProducts.isNotEmpty()
                }

    fun isError(field: Int): Boolean {
        if (field !in touchedFields) return false
        return when (field) {
            Constants.SERVICE_NAME_FIELD -> name.isBlank()
            Constants.SERVICE_CATEGORY_FIELD -> selectedCategory == null
            Constants.SERVICE_SALE_PRICE_FIELD -> salePrice.isBlank()
            Constants.SERVICE_ESTIMATED_COST_FIELD -> expenseMode == ExpenseMode.MANUAL && estimatedCost.isBlank()
            else -> false
        }
    }

    fun hasChangesFrom(initial: ServiceFormInputsState): Boolean {
        return name != initial.name ||
                selectedCategory?.id != initial.selectedCategory?.id ||
                salePrice != initial.salePrice ||
                expenseMode != initial.expenseMode ||
                estimatedCost != initial.estimatedCost ||
                selectedProducts != initial.selectedProducts
    }
}