package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import yosel.dev.atti.core.models.dto.PhysiologicalConstsDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class PhysiologicalConstsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun insertAndGetConstants(constants: PhysiologicalConstsDto): PhysiologicalConstsDto {
        return postgrest.from(Constants.PHYSIOLOGICAL_CONSTANTS_SUPABASE)
            .insert(constants) {
                select(
                    columns = Columns.raw(
                        """
                        *,
                        weight_unit:app_catalogs!weight_unit_catalog_id(*)
                        """.trimIndent()
                    )
                )
            }
            .decodeSingle<PhysiologicalConstsDto>()
    }

    suspend fun updateConstants(constants: PhysiologicalConstsDto): PhysiologicalConstsDto {
        return postgrest.from(Constants.PHYSIOLOGICAL_CONSTANTS_SUPABASE)
            .update(constants) {
                filter {
                    eq("id", constants.id ?: "")
                }
                select(
                    columns = Columns.raw(
                        """
                        *,
                        weight_unit:app_catalogs!weight_unit_catalog_id(*)
                        """.trimIndent()
                    )
                )
            }
            .decodeSingle<PhysiologicalConstsDto>()
    }

    suspend fun getConstantsWithDetailsById(id: String): PhysiologicalConstsDto? {
        return postgrest.from(Constants.PHYSIOLOGICAL_CONSTANTS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                    *,
                    weight_unit:app_catalogs!weight_unit_catalog_id(*)
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<PhysiologicalConstsDto>()
    }

    suspend fun getConstantsWithDetailsByConsultationId(consultationId: String): PhysiologicalConstsDto? {
        return postgrest.from(Constants.PHYSIOLOGICAL_CONSTANTS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                    *,
                    weight_unit:app_catalogs!weight_unit_catalog_id(*)
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
            .decodeSingleOrNull<PhysiologicalConstsDto>()
    }
}