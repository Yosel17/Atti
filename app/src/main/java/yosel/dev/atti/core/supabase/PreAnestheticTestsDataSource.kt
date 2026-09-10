package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.PreAnestheticTestDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class PreAnestheticTestsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    private val detailedColumns = Columns.raw(
        """
        *,
        product:products(
            *,
            supplier:suppliers(*),
            category:app_catalogs!category_id(*),
            unit_type:app_catalogs!unit_type_id(*)
        ),
        service:services(
            *,
            category:app_catalogs!category_id(*)
        )
        """.trimIndent()
    )

    suspend fun insertPreAnestheticTests(tests: List<PreAnestheticTestDto>): List<PreAnestheticTestDto> {
        if (tests.isEmpty()) return emptyList()
        return postgrest.from(Constants.PRE_ANESTHETIC_TESTS_SUPABASE)
            .insert(tests) {
                select(columns = detailedColumns)
            }
            .decodeList<PreAnestheticTestDto>()
    }

    suspend fun insertAndGetPreAnestheticTest(test: PreAnestheticTestDto): PreAnestheticTestDto {
        return postgrest.from(Constants.PRE_ANESTHETIC_TESTS_SUPABASE)
            .insert(test) {
                select(columns = detailedColumns)
            }
            .decodeSingle<PreAnestheticTestDto>()
    }

    suspend fun getPreAnestheticTestsWithDetailsByConsultationId(consultationId: String): List<PreAnestheticTestDto> {
        return postgrest.from(Constants.PRE_ANESTHETIC_TESTS_SUPABASE)
            .select(columns = detailedColumns) {
                filter {
                    eq("consultation_id", consultationId)
                    eq("status", Constants.ACTIVE_STATUS)
                }
                order("created_at", Order.ASCENDING)
            }
            .decodeList<PreAnestheticTestDto>()
    }

    suspend fun deletePreAnestheticTestsByConsultationId(consultationId: String) {
        postgrest.from(Constants.PRE_ANESTHETIC_TESTS_SUPABASE)
            .delete {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
    }

    suspend fun deletePreAnestheticTestById(id: String) {
        postgrest.from(Constants.PRE_ANESTHETIC_TESTS_SUPABASE)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun getPreAnestheticTestsByConsultationId(consultationId: String): List<PreAnestheticTestDto> {
        return postgrest.from(Constants.PRE_ANESTHETIC_TESTS_SUPABASE)
            .select {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
            .decodeList<PreAnestheticTestDto>()
    }
}