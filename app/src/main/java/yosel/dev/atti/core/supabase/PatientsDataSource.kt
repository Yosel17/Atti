package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import yosel.dev.atti.core.models.dto.PatientDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class PatientsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getAllPatients(): List<PatientDto>{
        return postgrest.from(Constants.PATIENTS_SUPABASE)
            .select()
            .decodeList<PatientDto>()
    }
}