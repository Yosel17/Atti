package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import yosel.dev.atti.core.models.dto.PrescriptionDto
import yosel.dev.atti.core.models.dto.PrescriptionItemDto
import yosel.dev.atti.core.models.request.CreatePrescriptionRequest
import yosel.dev.atti.core.models.request.UpdatePrescriptionRequest
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class PrescriptionsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun insertPrescriptionWithDetails(request: CreatePrescriptionRequest): PrescriptionDto {
        return postgrest.rpc(
            function = "create_prescription_with_details",
            parameters = request
        ).decodeAs<PrescriptionDto>()
    }

    suspend fun updatePrescriptionWithDetails(request: UpdatePrescriptionRequest): PrescriptionDto {
        return postgrest.rpc(
            function = "update_prescription_with_details",
            parameters = request
        ).decodeAs<PrescriptionDto>()
    }

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
                }
            }
            .decodeSingleOrNull<PrescriptionDto>()
    }

    suspend fun deletePrescriptionById(id: String) {
        postgrest.from(Constants.PRESCRIPTIONS_SUPABASE)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun deletePrescriptionByConsultationId(consultationId: String) {
        postgrest.from(Constants.PRESCRIPTIONS_SUPABASE)
            .delete {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
    }

    suspend fun getPrescriptionItemsByConsultationId(consultationId: String): List<PrescriptionItemDto> {
        val prescription = postgrest.from(Constants.PRESCRIPTIONS_SUPABASE)
            .select {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
            .decodeSingleOrNull<PrescriptionDto>() ?: return emptyList()

        return postgrest.from(Constants.PRESCRIPTION_ITEMS_SUPABASE)
            .select {
                filter {
                    eq("prescription_id", prescription.id ?: "")
                }
            }
            .decodeList<PrescriptionItemDto>()
    }
}