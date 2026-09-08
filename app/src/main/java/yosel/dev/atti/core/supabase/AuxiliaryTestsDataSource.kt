package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.AuxiliaryTestDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class AuxiliaryTestsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun insertAuxiliaryTests(tests: List<AuxiliaryTestDto>): List<AuxiliaryTestDto> {
        if (tests.isEmpty()) return emptyList()
        return postgrest.from(Constants.AUXILIARY_TESTS_SUPABASE)
            .insert(tests) {
                select(
                    columns = Columns.raw(
                        """
                        *,
                        catalog:app_catalogs!test_catalog_id(*)
                        """.trimIndent()
                    )
                )
            }
            .decodeList<AuxiliaryTestDto>()
    }

    suspend fun insertAndGetAuxiliaryTest(test: AuxiliaryTestDto): AuxiliaryTestDto {
        return postgrest.from(Constants.AUXILIARY_TESTS_SUPABASE)
            .insert(test) {
                select(
                    columns = Columns.raw(
                        """
                        *,
                        catalog:app_catalogs!test_catalog_id(*)
                        """.trimIndent()
                    )
                )
            }
            .decodeSingle<AuxiliaryTestDto>()
    }

    suspend fun getAuxiliaryTestsWithDetailsByConsultationId(consultationId: String): List<AuxiliaryTestDto> {
        return postgrest.from(Constants.AUXILIARY_TESTS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                    *,
                    catalog:app_catalogs!test_catalog_id(*)
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("consultation_id", consultationId)
                    eq("status", Constants.ACTIVE_STATUS)
                }
                order("created_at", Order.ASCENDING)
            }
            .decodeList<AuxiliaryTestDto>()
    }

    suspend fun deleteAuxiliaryTestsByConsultationId(consultationId: String) {
        postgrest.from(Constants.AUXILIARY_TESTS_SUPABASE)
            .delete {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
    }

    suspend fun deleteAuxiliaryTestById(id: String) {
        postgrest.from(Constants.AUXILIARY_TESTS_SUPABASE)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }
}