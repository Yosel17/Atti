package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatientDto(
    @SerialName("id")
    val id: String? = null,

    @SerialName("client_id")
    val clientId: String,

    @SerialName("name")
    val name: String,

    @SerialName("species_id")
    val speciesId: Int? = null, // Cambiado de String? a Int?

    @SerialName("gender_id")
    val genderId: Int? = null,  // Cambiado de String? a Int?

    @SerialName("breed")
    val breed: String? = null,

    @SerialName("age_years")
    val ageYears: Int? = 0,

    @SerialName("age_months")
    val ageMonths: Int? = 0,

    @SerialName("color")
    val color: String? = null,

    @SerialName("is_neutered")
    val isNeutered: Boolean? = false,

    @SerialName("photo_url")
    val photoUrl: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("status")
    val status: Int,

    // Relaciones mapeadas desde Supabase (Opcionales)
    @SerialName("species") val species: AppCatalogDto? = null,
    @SerialName("gender") val gender: AppCatalogDto? = null,
    @SerialName("client") val client: ClientDto? = null,
)
