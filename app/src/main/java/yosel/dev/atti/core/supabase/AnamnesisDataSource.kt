package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import yosel.dev.atti.core.models.dto.AnamnesisDewormingDto
import yosel.dev.atti.core.models.dto.AnamnesisDto
import yosel.dev.atti.core.models.dto.AnamnesisEnvironmentOptionDto
import yosel.dev.atti.core.models.dto.AnamnesisVaccineDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class AnamnesisDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getAnamnesisWithDetailsByConsultationId(consultationId: String): AnamnesisDto? {
        return postgrest.from(Constants.ANAMNESIS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                    *,
                    food_brand:app_catalogs!food_brand_id(*),
                    food_unit:app_catalogs!food_unit_type_id(*),
                    environment_options:anamnesis_environment_options(
                        *,
                        catalog:app_catalogs!catalog_id(*)
                    ),
                    vaccines:anamnesis_vaccines(
                        *,
                        vaccine:app_catalogs!vaccine_catalog_id(*),
                        scheme:app_catalogs!scheme_catalog_id(*)
                    ),
                    dewormings:anamnesis_dewormings(
                        *,
                        product:app_catalogs!product_catalog_id(*)
                    )
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
            .decodeSingleOrNull<AnamnesisDto>()
    }

    suspend fun insertAndGetAnamnesis(anamnesis: AnamnesisDto): AnamnesisDto {
        return postgrest.from(Constants.ANAMNESIS_SUPABASE)
            .insert(anamnesis) {
                select()
            }
            .decodeSingle<AnamnesisDto>()
    }

    suspend fun updateAnamnesis(anamnesis: AnamnesisDto) {
        postgrest.from(Constants.ANAMNESIS_SUPABASE)
            .update(anamnesis) {
                filter {
                    eq("id", anamnesis.id ?: "")
                }
            }
    }

    suspend fun insertEnvironmentOptions(options: List<AnamnesisEnvironmentOptionDto>): List<AnamnesisEnvironmentOptionDto> {
        if (options.isEmpty()) return emptyList()
        return postgrest.from(Constants.ANAMNESIS_ENV_OPTIONS_SUPABASE)
            .insert(options) {
                select()
            }
            .decodeList<AnamnesisEnvironmentOptionDto>()
    }

    suspend fun deleteEnvironmentOptionsByAnamnesisId(anamnesisId: String) {
        postgrest.from(Constants.ANAMNESIS_ENV_OPTIONS_SUPABASE)
            .delete {
                filter {
                    eq("anamnesis_id", anamnesisId)
                }
            }
    }

    suspend fun insertVaccines(vaccines: List<AnamnesisVaccineDto>): List<AnamnesisVaccineDto> {
        if (vaccines.isEmpty()) return emptyList()
        return postgrest.from(Constants.ANAMNESIS_VACCINES_SUPABASE)
            .insert(vaccines) {
                select()
            }
            .decodeList<AnamnesisVaccineDto>()
    }

    suspend fun deleteVaccinesByAnamnesisId(anamnesisId: String) {
        postgrest.from(Constants.ANAMNESIS_VACCINES_SUPABASE)
            .delete {
                filter {
                    eq("anamnesis_id", anamnesisId)
                }
            }
    }

    suspend fun insertDewormings(dewormings: List<AnamnesisDewormingDto>): List<AnamnesisDewormingDto> {
        if (dewormings.isEmpty()) return emptyList()
        return postgrest.from(Constants.ANAMNESIS_DEWORMINGS_SUPABASE)
            .insert(dewormings) {
                select()
            }
            .decodeList<AnamnesisDewormingDto>()
    }

    suspend fun deleteDewormingsByAnamnesisId(anamnesisId: String) {
        postgrest.from(Constants.ANAMNESIS_DEWORMINGS_SUPABASE)
            .delete {
                filter {
                    eq("anamnesis_id", anamnesisId)
                }
            }
    }
}