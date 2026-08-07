package yosel.dev.atti.screens.detail_client.ui

import yosel.dev.atti.core.utils.Constants

data class EditClientFormState(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val documentId: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val address: String = "",
    val createdAt: String = "",
    val touchedFields: Set<Int> = emptySet()
){
    val isValid: Boolean
        get() = firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                documentId.isNotBlank() &&
                phoneNumber.isNotBlank() &&
                address.isNotBlank()

    fun isError(field: Int): Boolean {
        if (field !in touchedFields) return false
        return when(field) {
            Constants.FIRST_NAME_FIELD -> firstName.isBlank()
            Constants.LAST_NAME_FIELD -> lastName.isBlank()
            Constants.DOCUMENT_ID_FIELD -> documentId.isBlank()
            Constants.PHONE_NUMBER_FIELD -> phoneNumber.isBlank()
            Constants.ADDRESS_FIELD -> address.isBlank()
            else -> false
        }
    }

    fun hasChangesFrom(initial: EditClientFormState): Boolean {
        return firstName != initial.firstName ||
                lastName != initial.lastName ||
                documentId != initial.documentId ||
                phoneNumber != initial.phoneNumber ||
                email != initial.email ||
                address != initial.address
    }
}
