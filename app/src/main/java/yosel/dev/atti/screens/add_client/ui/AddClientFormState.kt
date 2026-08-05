package yosel.dev.atti.screens.add_client.ui

data class AddClientFormState(
    val firstName: String = "",
    val lastName: String = "",
    val documentId: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val address: String = ""
){
    val isValid: Boolean
        get() = firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                documentId.isNotBlank() &&
                phoneNumber.isNotBlank() &&
                address.isNotBlank()
}
