package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import yosel.dev.atti.core.models.dto.FastingDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class FastingDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    private val detailedColumns = Columns.raw(
        """
        *,
        food_fasting:app_catalogs!food_fasting_catalog_id(*),
        water_fasting:app_catalogs!water_fasting_catalog_id(*)
        """.trimIndent()
    )

    suspend fun insertAndGetFasting(fasting: FastingDto): FastingDto {
        return postgrest.from(Constants.FASTING_SUPABASE)
            .insert(fasting) {
                select(columns = detailedColumns)
            }
            .decodeSingle<FastingDto>()
    }

    suspend fun updateFasting(fasting: FastingDto): FastingDto {
        return postgrest.from(Constants.FASTING_SUPABASE)
            .update(fasting) {
                filter {
                    eq("id", fasting.id ?: "")
                }
                select(columns = detailedColumns)
            }
            .decodeSingle<FastingDto>()
    }

    suspend fun getFastingWithDetailsByConsultationId(consultationId: String): FastingDto? {
        return postgrest.from(Constants.FASTING_SUPABASE)
            .select(columns = detailedColumns) {
                filter {
                    eq("consultation_id", consultationId)
                    eq("status", Constants.ACTIVE_STATUS)
                }
            }
            .decodeSingleOrNull<FastingDto>()
    }

    suspend fun getFastingWithDetailsById(id: String): FastingDto? {
        return postgrest.from(Constants.FASTING_SUPABASE)
            .select(columns = detailedColumns) {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<FastingDto>()
    }

    suspend fun deleteFastingByConsultationId(consultationId: String) {
        postgrest.from(Constants.FASTING_SUPABASE)
            .delete {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
    }

    suspend fun deleteFastingById(id: String) {
        postgrest.from(Constants.FASTING_SUPABASE)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }
}