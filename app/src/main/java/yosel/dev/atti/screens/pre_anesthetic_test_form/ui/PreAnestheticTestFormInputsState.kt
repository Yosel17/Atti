package yosel.dev.atti.screens.pre_anesthetic_test_form.ui

import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

enum class PreAnestheticTestTab {
    PRODUCTS,
    SERVICES
}

data class SelectedPreAnestheticProduct(
    val productWithDetails: ProductWithDetailsModel,
    val quantity: Int = 1
)

data class SelectedPreAnestheticService(
    val serviceWithDetails: ServiceWithDetailsModel,
    val quantity: Int = 1
)

data class PreAnestheticTestFormInputsState(
    val selectedProducts: List<SelectedPreAnestheticProduct> = emptyList(),
    val selectedServices: List<SelectedPreAnestheticService> = emptyList(),
    val notes: String = ""
) {
    val isValid: Boolean
        get() = selectedProducts.isNotEmpty() || selectedServices.isNotEmpty()

    val totalAmount: Double
        get() {
            val productsTotal = selectedProducts.sumOf { it.productWithDetails.product.salePrice * it.quantity }
            val servicesTotal = selectedServices.sumOf { it.serviceWithDetails.service.salePrice * it.quantity }
            return productsTotal + servicesTotal
        }

    fun hasChangesFrom(initial: PreAnestheticTestFormInputsState): Boolean {
        if (notes != initial.notes) return true
        if (selectedProducts.size != initial.selectedProducts.size ||
            selectedServices.size != initial.selectedServices.size
        ) {
            return true
        }
        val currentProdMap = selectedProducts.associate { it.productWithDetails.product.id to it.quantity }
        val initProdMap = initial.selectedProducts.associate { it.productWithDetails.product.id to it.quantity }
        if (currentProdMap != initProdMap) return true

        val currentServMap = selectedServices.associate { it.serviceWithDetails.service.id to it.quantity }
        val initServMap = initial.selectedServices.associate { it.serviceWithDetails.service.id to it.quantity }
        return currentServMap != initServMap
    }
}
