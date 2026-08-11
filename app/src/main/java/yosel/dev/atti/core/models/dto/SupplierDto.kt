package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupplierDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String,
    @SerialName("tax_id") val taxId: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int
)
