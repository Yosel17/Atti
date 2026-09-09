package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.AsaClassificationDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class AsaClassificationsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun insertAsaClassifications(classifications: List<AsaClassificationDto>): List<AsaClassificationDto> {
        if (classifications.isEmpty()) return emptyList()
        return postgrest.from(Constants.ASA_CLASSIFICATIONS_SUPABASE)
            .insert(classifications) {
                select(
                    columns = Columns.raw(
                        """
                        *,
                        catalog:app_catalogs!asa_catalog_id(*)
                        """.trimIndent()
                    )
                )
            }
            .decodeList<AsaClassificationDto>()
    }

    suspend fun insertAndGetAsaClassification(classification: AsaClassificationDto): AsaClassificationDto {
        return postgrest.from(Constants.ASA_CLASSIFICATIONS_SUPABASE)
            .insert(classification) {
                select(
                    columns = Columns.raw(
                        """
                        *,
                        catalog:app_catalogs!asa_catalog_id(*)
                        """.trimIndent()
                    )
                )
            }
            .decodeSingle<AsaClassificationDto>()
    }

    suspend fun getAsaClassificationsWithDetailsByConsultationId(consultationId: String): List<AsaClassificationDto> {
        return postgrest.from(Constants.ASA_CLASSIFICATIONS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                    *,
                    catalog:app_catalogs!asa_catalog_id(*)
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("consultation_id", consultationId)
                    eq("status", Constants.ACTIVE_STATUS)
                }
                order("created_at", Order.ASCENDING)
            }
            .decodeList<AsaClassificationDto>()
    }

    suspend fun deleteAsaClassificationsByConsultationId(consultationId: String) {
        postgrest.from(Constants.ASA_CLASSIFICATIONS_SUPABASE)
            .delete {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
    }

    suspend fun deleteAsaClassificationById(id: String) {
        postgrest.from(Constants.ASA_CLASSIFICATIONS_SUPABASE)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }
}