package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.ConsultationDto
import yosel.dev.atti.core.models.dto.ConsultationProgressDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class ConsultationsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun getConsultationsWithDetailsByStatus(status: Int): List<ConsultationDto> {
        return postgrest.from(Constants.CONSULTATIONS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                *,
                patient:patients(
                    *,
                    species:app_catalogs!species_id(*),
                    gender:app_catalogs!gender_id(*)
                ),
                consultation_type:app_catalogs!consultation_type_id(*)
                """.trimIndent()
                )
            ) {
                filter {
                    eq("status", status)
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<ConsultationDto>()
    }

    suspend fun getConsultationsWithDetailsById(id: String): ConsultationDto? {
        return postgrest.from(Constants.CONSULTATIONS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                *,
                patient:patients!patient_id(*),
                consultation_type:app_catalogs!consultation_type_id(*)
                """.trimIndent()
                )
            ) {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<ConsultationDto>()
    }

    suspend fun getConsultationsByPatientId(patientId: String): List<ConsultationDto> {
        return postgrest.from(Constants.CONSULTATIONS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                    *,
                    patient:patients!patient_id(*),
                    consultation_type:app_catalogs!consultation_type_id(*)
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("patient_id", patientId)
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<ConsultationDto>()
    }

    suspend fun getConsultationProgressById(consultationId: String): ConsultationProgressDto? {
        return postgrest.from(Constants.CONSULTATIONS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                id,
                status,
                consultation_type_id,
                anamnesis:anamnesis(id, status),
                clinical_examinations:clinical_examinations(id, status),
                physiological_constants:physiological_constants(id, status),
                diagnoses:diagnoses(id, status)
                """.trimIndent()
                )
            ) {
                filter {
                    eq("id", consultationId)
                }
            }
            .decodeSingleOrNull<ConsultationProgressDto>()
    }

    suspend fun insertAndGetConsultation(consultation: ConsultationDto): ConsultationDto {
        return postgrest.from(Constants.CONSULTATIONS_SUPABASE)
            .insert(consultation) {
                select()
            }
            .decodeSingle<ConsultationDto>()
    }

    suspend fun updateConsultation(consultation: ConsultationDto) {
        postgrest.from(Constants.CONSULTATIONS_SUPABASE)
            .update(consultation) {
                filter {
                    eq("id", consultation.id ?: "")
                }
            }
    }

    suspend fun updateConsultationStatus(consultationId: String, newStatus: Int) {
        postgrest.from(Constants.CONSULTATIONS_SUPABASE)
            .update(
                {
                    set("status", newStatus)
                }
            ) {
                filter {
                    eq("id", consultationId)
                }
            }
    }
}