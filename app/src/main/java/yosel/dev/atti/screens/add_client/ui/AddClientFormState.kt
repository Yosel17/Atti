package yosel.dev.atti.screens.add_client.ui

import yosel.dev.atti.core.utils.Constants

data class AddClientFormState(
    val firstName: String = "",
    val lastName: String = "",
    val documentId: String = "",
    val phoneNumber: String = "+502 ",
    val email: String = "",
    val address: String = "",
    val touchedFields: Set<Int> = emptySet()
){

    private val isPhoneValid: Boolean
        get() {
            val digitsOnly = phoneNumber.filter { it.isDigit() }
            return phoneNumber.isNotBlank() && digitsOnly.length >= 4
        }

    val isValid: Boolean
        get() = firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                documentId.isNotBlank() &&
                isPhoneValid &&
                address.isNotBlank()

    fun isError(field: Int): Boolean {
        if (field !in touchedFields) return false
        return when (field) {
            Constants.FIRST_NAME_FIELD -> firstName.isBlank()
            Constants.LAST_NAME_FIELD -> lastName.isBlank()
            Constants.DOCUMENT_ID_FIELD -> documentId.isBlank()
            Constants.PHONE_NUMBER_FIELD -> !isPhoneValid
            Constants.ADDRESS_FIELD -> address.isBlank()
            else -> false
        }
    }
}
