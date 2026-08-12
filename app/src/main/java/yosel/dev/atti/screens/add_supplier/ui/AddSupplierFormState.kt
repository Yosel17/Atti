package yosel.dev.atti.screens.add_supplier.ui

import yosel.dev.atti.core.utils.Constants

data class AddSupplierFormState(
    val name: String = "",
    val taxId: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val touchedFields: Set<Int> = emptySet()
) {
    val isValid: Boolean
        get() = name.isNotBlank() &&
                taxId.isNotBlank() &&
                phoneNumber.isNotBlank() &&
                address.isNotBlank()

    fun isError(field: Int): Boolean {
        if (field !in touchedFields) return false
        return when (field) {
            Constants.SUPPLIER_NAME_FIELD -> name.isBlank()
            Constants.SUPPLIER_TAX_ID_FIELD -> taxId.isBlank()
            Constants.SUPPLIER_PHONE_FIELD -> phoneNumber.isBlank()
            Constants.SUPPLIER_ADDRESS_FIELD -> address.isBlank()
            else -> false
        }
    }
}
