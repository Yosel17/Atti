package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import yosel.dev.atti.core.models.dto.AnamnesisDto
import yosel.dev.atti.core.models.request.CreateAnamnesisRequest
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class AnamnesisDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun insertAnamnesisWithDetails(request: CreateAnamnesisRequest): AnamnesisDto {
        return postgrest.rpc(
            function = "create_anamnesis_with_details",
            parameters = request
        ).decodeAs<AnamnesisDto>()
    }

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

    suspend fun updateAnamnesis(anamnesis: AnamnesisDto) {
        postgrest.from(Constants.ANAMNESIS_SUPABASE)
            .update(anamnesis) {
                filter {
                    eq("id", anamnesis.id ?: "")
                }
            }
    }
}