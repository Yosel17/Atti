package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.DiagnosisDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class DiagnosesDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun insertDiagnoses(diagnoses: List<DiagnosisDto>): List<DiagnosisDto> {
        if (diagnoses.isEmpty()) return emptyList()
        return postgrest.from(Constants.DIAGNOSES_SUPABASE)
            .insert(diagnoses) {
                select(
                    columns = Columns.raw(
                        """
                        *,
                        catalog:app_catalogs!diagnosis_catalog_id(*)
                        """.trimIndent()
                    )
                )
            }
            .decodeList<DiagnosisDto>()
    }

    suspend fun insertAndGetDiagnosis(diagnosis: DiagnosisDto): DiagnosisDto {
        return postgrest.from(Constants.DIAGNOSES_SUPABASE)
            .insert(diagnosis) {
                select(
                    columns = Columns.raw(
                        """
                        *,
                        catalog:app_catalogs!diagnosis_catalog_id(*)
                        """.trimIndent()
                    )
                )
            }
            .decodeSingle<DiagnosisDto>()
    }

    suspend fun getDiagnosesWithDetailsByConsultationId(consultationId: String): List<DiagnosisDto> {
        return postgrest.from(Constants.DIAGNOSES_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                    *,
                    catalog:app_catalogs!diagnosis_catalog_id(*)
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("consultation_id", consultationId)
                    eq("status", Constants.ACTIVE_STATUS)
                }
                order("created_at", Order.ASCENDING)
            }
            .decodeList<DiagnosisDto>()
    }

    suspend fun deleteDiagnosesByConsultationId(consultationId: String) {
        postgrest.from(Constants.DIAGNOSES_SUPABASE)
            .delete {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
    }

    suspend fun deleteDiagnosisById(id: String) {
        postgrest.from(Constants.DIAGNOSES_SUPABASE)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }
}