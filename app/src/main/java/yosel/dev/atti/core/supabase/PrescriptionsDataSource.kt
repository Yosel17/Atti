package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import yosel.dev.atti.core.models.dto.PrescriptionDto
import yosel.dev.atti.core.models.dto.PrescriptionItemDto
import yosel.dev.atti.core.models.request.SavePrescriptionRequest
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class PrescriptionsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    // 1. Obtener receta completa por ID de consulta con los productos detallados
    suspend fun getPrescriptionWithDetailsByConsultationId(consultationId: String): PrescriptionDto? {
        return postgrest.from(Constants.PRESCRIPTIONS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                    *,
                    prescription_items:prescription_items(
                        *,
                        product:products(
                            *,
                            supplier:suppliers(*),
                            category:app_catalogs!category_id(*),
                            unit_type:app_catalogs!unit_type_id(*)
                        )
                    )
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("consultation_id", consultationId)
                    eq("status", Constants.ACTIVE_STATUS)
                }
            }
            .decodeSingleOrNull<PrescriptionDto>()
    }

    // 2. Obtener por ID de receta
    suspend fun getPrescriptionWithDetailsById(prescriptionId: String): PrescriptionDto? {
        return postgrest.from(Constants.PRESCRIPTIONS_SUPABASE)
            .select(
                columns = Columns.raw(
                    """
                    *,
                    prescription_items:prescription_items(
                        *,
                        product:products(
                            *,
                            supplier:suppliers(*),
                            category:app_catalogs!category_id(*),
                            unit_type:app_catalogs!unit_type_id(*)
                        )
                    )
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("id", prescriptionId)
                }
            }
            .decodeSingleOrNull<PrescriptionDto>()
    }

    // 3. Inserción directa / reemplazo de ítems (sin necesidad de RPC obligatoria)
    suspend fun insertPrescription(prescription: PrescriptionDto): PrescriptionDto {
        return postgrest.from(Constants.PRESCRIPTIONS_SUPABASE)
            .insert(prescription) {
                select()
            }
            .decodeSingle<PrescriptionDto>()
    }

    suspend fun updatePrescription(prescription: PrescriptionDto): PrescriptionDto {
        return postgrest.from(Constants.PRESCRIPTIONS_SUPABASE)
            .update(prescription) {
                filter {
                    eq("id", prescription.id.orEmpty())
                }
                select()
            }
            .decodeSingle<PrescriptionDto>()
    }

    suspend fun insertPrescriptionItems(items: List<PrescriptionItemDto>): List<PrescriptionItemDto> {
        if (items.isEmpty()) return emptyList()
        return postgrest.from(Constants.PRESCRIPTION_ITEMS_SUPABASE)
            .insert(items) {
                select()
            }
            .decodeList<PrescriptionItemDto>()
    }

    suspend fun deleteItemsByPrescriptionId(prescriptionId: String) {
        postgrest.from(Constants.PRESCRIPTION_ITEMS_SUPABASE)
            .delete {
                filter {
                    eq("prescription_id", prescriptionId)
                }
            }
    }

    suspend fun deletePrescriptionById(id: String) {
        postgrest.from(Constants.PRESCRIPTIONS_SUPABASE)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }

    // 4. Si prefieres usar una función RPC en PostgreSQL (al estilo anamnesis)
    suspend fun savePrescriptionRpc(request: SavePrescriptionRequest): PrescriptionDto {
        return postgrest.rpc(
            function = "save_prescription_with_items",
            parameters = request
        ).decodeAs<PrescriptionDto>()
    }
}