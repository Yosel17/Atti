package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import yosel.dev.atti.core.models.dto.TreatmentDto
import yosel.dev.atti.core.models.request.ReplaceTreatmentsRequest
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class TreatmentsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun insertTreatments(treatments: List<TreatmentDto>): List<TreatmentDto> {
        if (treatments.isEmpty()) return emptyList()
        return postgrest.from(Constants.TREATMENTS_SUPABASE)
            .insert(treatments) {
                select(
                    columns = Columns.raw(
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
                )
            }
            .decodeList<TreatmentDto>()
    }

    suspend fun insertAndGetTreatment(treatment: TreatmentDto): TreatmentDto {
        return postgrest.from(Constants.TREATMENTS_SUPABASE)
            .insert(treatment) {
                select(
                    columns = Columns.raw(
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
                )
            }
            .decodeSingle<TreatmentDto>()
    }

    suspend fun getTreatmentsWithDetailsByConsultationId(consultationId: String): List<TreatmentDto> {
        return postgrest.from(Constants.TREATMENTS_SUPABASE)
            .select(
                columns = Columns.raw(
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
            ) {
                filter {
                    eq("consultation_id", consultationId)
                    eq("status", Constants.ACTIVE_STATUS)
                }
                order("created_at", Order.ASCENDING)
            }
            .decodeList<TreatmentDto>()
    }

    suspend fun deleteTreatmentsByConsultationId(consultationId: String) {
        postgrest.from(Constants.TREATMENTS_SUPABASE)
            .delete {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
    }

    suspend fun deleteTreatmentById(id: String) {
        postgrest.from(Constants.TREATMENTS_SUPABASE)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun replaceTreatmentsRpc(
        consultationId: String,
        treatments: List<TreatmentDto>
    ): List<TreatmentDto> {
        return postgrest.rpc(
            function = "replace_consultation_treatments",
            parameters = ReplaceTreatmentsRequest(
                consultationId = consultationId,
                treatments = treatments
            )
        ).decodeAs<List<TreatmentDto>>()
    }
}
