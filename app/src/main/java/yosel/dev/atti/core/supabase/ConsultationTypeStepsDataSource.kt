package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.ConsultationTypeStepDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class ConsultationTypeStepsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun getStepsByConsultationTypeId(consultationTypeId: Int): List<ConsultationTypeStepDto> {
        return postgrest.from(Constants.CONSULTATION_TYPE_STEPS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                    *,
                    consultation_type:app_catalogs!consultation_type_id(*),
                    step_catalog:app_catalogs!step_catalog_id(*)
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("consultation_type_id", consultationTypeId)
                }
                order("step_order", Order.ASCENDING)
            }
            .decodeList<ConsultationTypeStepDto>()
    }
}
