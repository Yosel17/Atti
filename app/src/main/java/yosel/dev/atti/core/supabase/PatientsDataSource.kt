package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.PatientDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class PatientsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getAllPatients(): List<PatientDto>{
        return postgrest.from(Constants.PATIENTS_SUPABASE)
            .select{
                order("status", Order.ASCENDING)
                order("created_at", Order.DESCENDING)
            }
            .decodeList<PatientDto>()
    }

    suspend fun getPatientsByClientId(clientId: String): List<PatientDto> {
        return postgrest.from(Constants.PATIENTS_SUPABASE)
            .select {
                filter {
                    eq("client_id", clientId)
                }
            }
            .decodeList<PatientDto>()
    }

    suspend fun insertAndGetPatient(patient: PatientDto): PatientDto {
        return postgrest.from(Constants.PATIENTS_SUPABASE)
            .insert(patient) {
                select()
            }
            .decodeSingle<PatientDto>()
    }

    suspend fun updatePatient(patient: PatientDto) {
        postgrest.from(Constants.PATIENTS_SUPABASE)
            .update(patient) {
                filter {
                    eq("id", patient.id ?: "")
                }
            }
    }

    suspend fun updatePatientStatus(patientId: String, newStatus: Int) {
        postgrest.from(Constants.PATIENTS_SUPABASE)
            .update(
                {
                    set("status", newStatus)
                }
            ) {
                filter {
                    eq("id", patientId)
                }
            }
    }

    suspend fun updatePatientsStatus(patientIds: List<String>, newStatus: Int) {

        if (patientIds.isEmpty()) return

        postgrest.from(Constants.PATIENTS_SUPABASE)
            .update(
                {
                    set("status", newStatus)
                }
            ) {
                filter {
                    isIn("id", patientIds)
                }
            }
    }
}