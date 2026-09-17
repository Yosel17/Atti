package yosel.dev.atti.core.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnamnesisDto(
    @SerialName("id") val id: String? = null,
    @SerialName("consultation_id") val consultationId: String,
    @SerialName("has_outdoor_access") val hasOutdoorAccess: Boolean = false,
    @SerialName("housemates") val housemates: String? = null,
    @SerialName("food_brand_id") val foodBrandId: Int? = null,
    @SerialName("food_quantity") val foodQuantity: Double? = 0.0,
    @SerialName("food_unit_type_id") val foodUnitTypeId: Int? = null,
    @SerialName("homemade_food") val homemadeFood: String? = null,
    @SerialName("feeding_frequency") val feedingFrequency: String? = null,
    @SerialName("water_consumption") val waterConsumption: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("status") val status: Int = 1,
    @SerialName("litter_brand_id") val litterBrandId: Int? = null,
    @SerialName("litter_quantity") val litterQuantity: Double? = 0.0,
    @SerialName("litter_unit_type_id") val litterUnitTypeId: Int? = null,
    @SerialName("comment") val comment: String? = null,
    // Relaciones mapeadas desde Supabase (Opcionales)
    @SerialName("food_brand") val foodBrand: AppCatalogDto? = null,
    @SerialName("food_unit") val foodUnit: AppCatalogDto? = null,
    @SerialName("environment_options") val environmentOptions: List<AnamnesisEnvironmentOptionDto> = emptyList(),
    @SerialName("vaccines") val vaccines: List<AnamnesisVaccineDto> = emptyList(),
    @SerialName("dewormings") val dewormings: List<AnamnesisDewormingDto> = emptyList(),
    @SerialName("litter_brand") val litterBrand: AppCatalogDto? = null,
    @SerialName("litter_unit") val litterUnit: AppCatalogDto? = null,
)
