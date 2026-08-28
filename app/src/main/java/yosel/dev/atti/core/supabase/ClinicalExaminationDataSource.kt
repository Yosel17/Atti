package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import yosel.dev.atti.core.models.dto.ClinicalExaminationDto
import yosel.dev.atti.core.models.request.CreateClinicalExamRequest
import yosel.dev.atti.core.models.request.UpdateClinicalExamRequest
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class ClinicalExaminationDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun insertClinicalExamWithDetails(request: CreateClinicalExamRequest): ClinicalExaminationDto {
        return postgrest.rpc(
            function = "create_clinical_exam_with_details",
            parameters = request
        ).decodeAs<ClinicalExaminationDto>()
    }

    suspend fun updateClinicalExamWithDetails(request: UpdateClinicalExamRequest): ClinicalExaminationDto {
        return postgrest.rpc(
            function = "update_clinical_exam_with_details",
            parameters = request
        ).decodeAs<ClinicalExaminationDto>()
    }

    suspend fun getClinicalExamWithDetailsById(examId: String): ClinicalExaminationDto? {
        return postgrest.from(Constants.CLINICAL_EXAMINATIONS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                    *,
                    coat:app_catalogs!coat_catalog_id(*),
                    lymph_nodes:clinical_examination_lymph_nodes(
                        *,
                        catalog:app_catalogs!catalog_id(*)
                    )
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("id", examId)
                }
            }
            .decodeSingleOrNull<ClinicalExaminationDto>()
    }

    suspend fun getClinicalExamWithDetailsByConsultationId(consultationId: String): ClinicalExaminationDto? {
        return postgrest.from(Constants.CLINICAL_EXAMINATIONS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                    *,
                    coat:app_catalogs!coat_catalog_id(*),
                    lymph_nodes:clinical_examination_lymph_nodes(
                        *,
                        catalog:app_catalogs!catalog_id(*)
                    )
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
            .decodeSingleOrNull<ClinicalExaminationDto>()
    }
}