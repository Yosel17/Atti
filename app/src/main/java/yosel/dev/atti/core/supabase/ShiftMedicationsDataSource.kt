package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import yosel.dev.atti.core.models.dto.ShiftMedicationDto
import yosel.dev.atti.core.models.request.ReplaceShiftMedicationsRequest
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class ShiftMedicationsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    private val detailedColumns = Columns.raw(
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

    suspend fun insertShiftMedications(medications: List<ShiftMedicationDto>): List<ShiftMedicationDto> {
        if (medications.isEmpty()) return emptyList()
        return postgrest.from(Constants.SHIFT_MEDICATIONS_SUPABASE)
            .insert(medications) {
                select(columns = detailedColumns)
            }
            .decodeList<ShiftMedicationDto>()
    }

    suspend fun insertAndGetShiftMedication(medication: ShiftMedicationDto): ShiftMedicationDto {
        return postgrest.from(Constants.SHIFT_MEDICATIONS_SUPABASE)
            .insert(medication) {
                select(columns = detailedColumns)
            }
            .decodeSingle<ShiftMedicationDto>()
    }

    suspend fun deleteShiftMedicationsByConsultationId(consultationId: String) {
        postgrest.from(Constants.SHIFT_MEDICATIONS_SUPABASE)
            .delete {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
    }

    suspend fun deleteShiftMedicationById(id: String) {
        postgrest.from(Constants.SHIFT_MEDICATIONS_SUPABASE)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun replaceShiftMedicationsRpc(
        consultationId: String,
        medications: List<ShiftMedicationDto>
    ): List<ShiftMedicationDto> {
        return postgrest.rpc(
            function = "replace_consultation_shift_medications",
            parameters = ReplaceShiftMedicationsRequest(
                consultationId = consultationId,
                medications = medications
            )
        ).decodeAs<List<ShiftMedicationDto>>()
    }

    suspend fun getShiftMedicationsByConsultationId(consultationId: String): List<ShiftMedicationDto> {
        return postgrest.from(Constants.SHIFT_MEDICATIONS_SUPABASE)
            .select(columns = detailedColumns) {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
            .decodeList<ShiftMedicationDto>()
    }
}