package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientDto(
    @SerialName("id") val id: String? = null,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("document_id") val documentId: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)
