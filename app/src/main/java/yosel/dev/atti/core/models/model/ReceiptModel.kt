package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate
import yosel.dev.atti.core.utils.formatPrice

data class ReceiptModel(
    val id: Long = 0L,
    val consultationId: String? = null,
    val customerName: String = "",
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val notes: String = "",
    val createdAt: String = "",
    val status: Int = 1
) {
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)

    val formattedSubtotal: String
        get() = subtotal.formatPrice()

    val formattedTotal: String
        get() = total.formatPrice()

    val formattedCorrelative: String
        get() = "#${id.toString().padStart(5, '0')}"
}
