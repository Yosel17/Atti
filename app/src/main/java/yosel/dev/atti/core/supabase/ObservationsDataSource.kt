package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import yosel.dev.atti.core.models.dto.ObservationDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class ObservationsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun insertAndGetObservation(observation: ObservationDto): ObservationDto {
        return postgrest.from(Constants.OBSERVATIONS_SUPABASE)
            .insert(observation) {
                select()
            }
            .decodeSingle<ObservationDto>()
    }

    suspend fun updateObservation(observation: ObservationDto): ObservationDto {
        return postgrest.from(Constants.OBSERVATIONS_SUPABASE)
            .update(observation) {
                filter {
                    eq("id", observation.id ?: "")
                }
                select()
            }
            .decodeSingle<ObservationDto>()
    }

    suspend fun getObservationByConsultationId(consultationId: String): ObservationDto? {
        return postgrest.from(Constants.OBSERVATIONS_SUPABASE)
            .select {
                filter {
                    eq("consultation_id", consultationId)
                    eq("status", Constants.ACTIVE_STATUS)
                }
            }
            .decodeSingleOrNull<ObservationDto>()
    }

    suspend fun getObservationById(id: String): ObservationDto? {
        return postgrest.from(Constants.OBSERVATIONS_SUPABASE)
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<ObservationDto>()
    }

    suspend fun deleteObservationById(id: String) {
        postgrest.from(Constants.OBSERVATIONS_SUPABASE)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun deleteObservationByConsultationId(consultationId: String) {
        postgrest.from(Constants.OBSERVATIONS_SUPABASE)
            .delete {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
    }
}