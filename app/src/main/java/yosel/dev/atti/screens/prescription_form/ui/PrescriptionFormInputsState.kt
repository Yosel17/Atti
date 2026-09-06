package yosel.dev.atti.screens.prescription_form.ui

import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import java.util.UUID

data class SelectedPrescriptionItem(
    val localId: String = UUID.randomUUID().toString(),
    val productWithDetails: ProductWithDetailsModel? = null,
    val customProductName: String = "",
    val instructions: String = "",
    val quantity: Int = 1
) {
    val isCustom: Boolean
        get() = productWithDetails == null

    val displayName: String
        get() = productWithDetails?.product?.commercialName ?: customProductName

    val subtitle: String
        get() = productWithDetails?.let {
            val cat = it.category.name.ifBlank { "PRODUCTO" }
            val unit = it.unitType.name
            if (unit.isNotBlank()) "$unit / $cat".uppercase() else cat.uppercase()
        } ?: "FUERA DE INVENTARIO"

    val maxStock: Int
        get() = productWithDetails?.product?.stock ?: Int.MAX_VALUE

    val unitPrice: Double
        get() = productWithDetails?.product?.salePrice ?: 0.0

    val totalPrice: Double
        get() = unitPrice * quantity
}

data class PrescriptionFormInputsState(
    val selectedItems: List<SelectedPrescriptionItem> = emptyList(),
    val generalNotes: String = ""
) {
    val isValid: Boolean
        get() = selectedItems.isNotEmpty()

    fun hasChangesFrom(initial: PrescriptionFormInputsState): Boolean {
        if (generalNotes.trim() != initial.generalNotes.trim()) return true
        if (selectedItems.size != initial.selectedItems.size) return true
        for (i in selectedItems.indices) {
            val cur = selectedItems[i]
            val init = initial.selectedItems.getOrNull(i) ?: return true
            if (cur.productWithDetails?.product?.id != init.productWithDetails?.product?.id ||
                cur.customProductName.trim() != init.customProductName.trim() ||
                cur.instructions.trim() != init.instructions.trim() ||
                cur.quantity != init.quantity
            ) {
                return true
            }
        }
        return false
    }
}