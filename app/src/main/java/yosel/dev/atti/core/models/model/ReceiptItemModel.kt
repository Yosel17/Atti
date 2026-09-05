package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate
import yosel.dev.atti.core.utils.formatPrice

data class ReceiptItemModel(
    val id: String = "",
    val receiptId: String = "",
    val productId: String? = null,
    val serviceId: String? = null,
    val quantity: Double = 1.0,
    val unitPrice: Double = 0.0,
    val subtotal: Double = 0.0,
    val createdAt: String = "",
    val status: Int = 1
) {
    val isProduct: Boolean
        get() = !productId.isNullOrBlank()

    val isService: Boolean
        get() = !serviceId.isNullOrBlank()

    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)

    val formattedUnitPrice: String
        get() = unitPrice.formatPrice()

    val formattedSubtotal: String
        get() = subtotal.formatPrice()
}
