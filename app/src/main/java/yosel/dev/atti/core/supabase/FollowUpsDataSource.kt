package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.FollowUpDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class FollowUpsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    // Columnas completas con Joins de Paciente y Consulta
    private val detailedColumns = Columns.raw(
        """
        *,
        patient:patients!patient_id(
            *,
            species:app_catalogs!species_id(*),
            gender:app_catalogs!gender_id(*)
        ),
        consultation:consultations!consultation_id(
            *,
            consultation_type:app_catalogs!consultation_type_id(*),
            patient:patients!patient_id(
                *,
                species:app_catalogs!species_id(*),
                gender:app_catalogs!gender_id(*)
            )
        )
        """.trimIndent()
    )

    suspend fun insertAndGetFollowUp(followUp: FollowUpDto): FollowUpDto {
        return postgrest.from(Constants.FOLLOW_UPS_SUPABASE)
            .insert(followUp) {
                select(columns = detailedColumns)
            }
            .decodeSingle<FollowUpDto>()
    }

    suspend fun updateFollowUp(followUp: FollowUpDto): FollowUpDto {
        return postgrest.from(Constants.FOLLOW_UPS_SUPABASE)
            .update(followUp) {
                filter {
                    eq("id", followUp.id ?: "")
                }
                select(columns = detailedColumns)
            }
            .decodeSingle<FollowUpDto>()
    }

    suspend fun getFollowUpWithDetailsById(id: String): FollowUpDto? {
        return postgrest.from(Constants.FOLLOW_UPS_SUPABASE)
            .select(columns = detailedColumns) {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<FollowUpDto>()
    }

    suspend fun getFollowUpsWithDetailsByConsultationId(consultationId: String): List<FollowUpDto> {
        return postgrest.from(Constants.FOLLOW_UPS_SUPABASE)
            .select(columns = detailedColumns) {
                filter {
                    eq("consultation_id", consultationId)
                    eq("status", Constants.ACTIVE_STATUS)
                }
                order("scheduled_at", Order.ASCENDING)
            }
            .decodeList<FollowUpDto>()
    }

    suspend fun getFollowUpsWithDetailsByPatientId(patientId: String): List<FollowUpDto> {
        return postgrest.from(Constants.FOLLOW_UPS_SUPABASE)
            .select(columns = detailedColumns) {
                filter {
                    eq("patient_id", patientId)
                    eq("status", Constants.ACTIVE_STATUS)
                }
                order("scheduled_at", Order.ASCENDING)
            }
            .decodeList<FollowUpDto>()
    }

    // Filtrar por rango de fecha/hora (ej. citas de un día o un mes en específico)
    suspend fun getFollowUpsByDateRange(
        startDateIso: String,
        endDateIso: String
    ): List<FollowUpDto> {
        return postgrest.from(Constants.FOLLOW_UPS_SUPABASE)
            .select(columns = detailedColumns) {
                filter {
                    gte("scheduled_at", startDateIso)
                    lte("scheduled_at", endDateIso)
                    eq("status", Constants.ACTIVE_STATUS)
                }
                order("scheduled_at", Order.ASCENDING)
            }
            .decodeList<FollowUpDto>()
    }

    suspend fun deleteFollowUpById(id: String) {
        postgrest.from(Constants.FOLLOW_UPS_SUPABASE)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun deleteFollowUpsByConsultationId(consultationId: String) {
        postgrest.from(Constants.FOLLOW_UPS_SUPABASE)
            .delete {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
    }
}
