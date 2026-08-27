package yosel.dev.atti.core.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import yosel.dev.atti.core.models.dto.AnamnesisDewormingDto
import yosel.dev.atti.core.models.dto.AnamnesisDto
import yosel.dev.atti.core.models.dto.AnamnesisEnvironmentOptionDto
import yosel.dev.atti.core.models.dto.AnamnesisVaccineDto

@Serializable
data class UpdateAnamnesisRequest(
    @SerialName("anamnesis_data")
    val anamnesisData: AnamnesisDto,
    @SerialName("environment_options_data")
    val environmentOptionsData: List<AnamnesisEnvironmentOptionDto>? = null,
    @SerialName("vaccines_data")
    val vaccinesData: List<AnamnesisVaccineDto>? = null,
    @SerialName("dewormings_data")
    val dewormingsData: List<AnamnesisDewormingDto>? = null
)
