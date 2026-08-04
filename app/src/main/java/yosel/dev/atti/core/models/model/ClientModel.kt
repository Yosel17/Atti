package yosel.dev.atti.core.models.model

import yosel.dev.atti.core.utils.formatDate

data class ClientModel(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val documentId: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val address: String = "",
    val createdAt: String = ""
){
    val formattedCreatedAt: String
        get() = formatDate(isoString = createdAt)
}
