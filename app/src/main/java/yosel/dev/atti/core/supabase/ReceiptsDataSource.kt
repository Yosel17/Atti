package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import yosel.dev.atti.core.models.dto.ReceiptDto
import yosel.dev.atti.core.models.request.CreateReceiptRequest
import yosel.dev.atti.core.models.request.UpdateReceiptRequest
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class ReceiptsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    private val detailedColumns = Columns.raw(
        """
        *,
        consultation:consultations(
            *,
            patient:patients(
                *,
                species:app_catalogs!species_id(*),
                gender:app_catalogs!gender_id(*)
            ),
            consultation_type:app_catalogs!consultation_type_id(*)
        ),
        receipt_items:receipt_items(
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
        )
        """.trimIndent()
    )

    suspend fun insertReceiptWithDetails(request: CreateReceiptRequest): ReceiptDto {
        return postgrest.rpc(
            function = "create_receipt_with_details",
            parameters = request
        ).decodeAs<ReceiptDto>()
    }

    suspend fun updateReceiptWithDetails(request: UpdateReceiptRequest): ReceiptDto {
        return postgrest.rpc(
            function = "update_receipt_with_details",
            parameters = request
        ).decodeAs<ReceiptDto>()
    }

    suspend fun getAllReceiptsWithDetails(): List<ReceiptDto> {
        return postgrest.from(Constants.RECEIPTS_SUPABASE)
            .select(columns = detailedColumns) {
                order("id", Order.DESCENDING)
            }
            .decodeList<ReceiptDto>()
    }

    suspend fun getReceiptWithDetailsById(receiptId: Long): ReceiptDto? {
        return postgrest.from(Constants.RECEIPTS_SUPABASE)
            .select(columns = detailedColumns) {
                filter {
                    eq("id", receiptId)
                }
            }
            .decodeSingleOrNull<ReceiptDto>()
    }

    suspend fun getReceiptWithDetailsByConsultationId(consultationId: String): ReceiptDto? {
        return postgrest.from(Constants.RECEIPTS_SUPABASE)
            .select(columns = detailedColumns) {
                filter {
                    eq("consultation_id", consultationId)
                    eq("status", Constants.ACTIVE_STATUS)
                }
            }
            .decodeSingleOrNull<ReceiptDto>()
    }

    suspend fun deleteReceiptById(receiptId: Long) {
        postgrest.from(Constants.RECEIPTS_SUPABASE)
            .delete {
                filter {
                    eq("id", receiptId)
                }
            }
    }

    suspend fun deleteReceiptByConsultationId(consultationId: String) {
        postgrest.from(Constants.RECEIPTS_SUPABASE)
            .delete {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
    }
}