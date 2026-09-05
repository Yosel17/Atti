package yosel.dev.atti.screens.receipt_form.ui

import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

enum class ReceiptTab {
    PRODUCTS,
    SERVICES
}

data class SelectedReceiptProduct(
    val productWithDetails: ProductWithDetailsModel,
    val quantity: Int = 1
)

data class SelectedReceiptService(
    val serviceWithDetails: ServiceWithDetailsModel,
    val quantity: Int = 1
)

data class ReceiptFormInputsState(
    val customerName: String = "",
    val selectedProducts: List<SelectedReceiptProduct> = emptyList(),
    val selectedServices: List<SelectedReceiptService> = emptyList()
) {
    val isValid: Boolean
        get() = selectedProducts.isNotEmpty() || selectedServices.isNotEmpty()

    val subtotalAmount: Double
        get() {
            val prodTotal = selectedProducts.sumOf { it.productWithDetails.product.salePrice * it.quantity }
            val servTotal = selectedServices.sumOf { it.serviceWithDetails.service.salePrice * it.quantity }
            return prodTotal + servTotal
        }

    val totalAmount: Double
        get() = subtotalAmount

    fun hasChangesFrom(initial: ReceiptFormInputsState): Boolean {
        if (customerName.trim() != initial.customerName.trim()) return true
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