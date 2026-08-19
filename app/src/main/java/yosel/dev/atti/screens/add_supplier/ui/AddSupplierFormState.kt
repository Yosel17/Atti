package yosel.dev.atti.screens.add_supplier.ui

import yosel.dev.atti.core.utils.Constants

data class AddSupplierFormState(
    val name: String = "",
    val taxId: String = "",
    val phoneNumber: String = "+502 ",
    val address: String = "",
    val touchedFields: Set<Int> = emptySet()
) {

    private val isPhoneValid: Boolean
        get() {
            val digitsOnly = phoneNumber.filter { it.isDigit() }
            return phoneNumber.isNotBlank() && digitsOnly.length >= 4
        }

    val isValid: Boolean
        get() = name.isNotBlank() &&
                taxId.isNotBlank() &&
                isPhoneValid &&
                address.isNotBlank()

    fun isError(field: Int): Boolean {
        if (field !in touchedFields) return false
        return when (field) {
            Constants.SUPPLIER_NAME_FIELD -> name.isBlank()
            Constants.SUPPLIER_TAX_ID_FIELD -> taxId.isBlank()
            Constants.SUPPLIER_PHONE_FIELD -> !isPhoneValid
            Constants.SUPPLIER_ADDRESS_FIELD -> address.isBlank()
            else -> false
        }
    }
}
