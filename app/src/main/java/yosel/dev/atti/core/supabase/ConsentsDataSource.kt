package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import yosel.dev.atti.core.models.dto.ConsentDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class ConsentsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun insertAndGetConsent(consent: ConsentDto): ConsentDto {
        return postgrest.from(Constants.CONSENTS_SUPABASE)
            .insert(consent) {
                select()
            }
            .decodeSingle<ConsentDto>()
    }

    suspend fun updateConsent(consent: ConsentDto): ConsentDto {
        return postgrest.from(Constants.CONSENTS_SUPABASE)
            .update(consent) {
                filter {
                    eq("id", consent.id ?: "")
                }
                select()
            }
            .decodeSingle<ConsentDto>()
    }

    suspend fun getConsentByConsultationId(consultationId: String): ConsentDto? {
        return postgrest.from(Constants.CONSENTS_SUPABASE)
            .select {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
            .decodeSingleOrNull<ConsentDto>()
    }

    suspend fun getConsentById(id: String): ConsentDto? {
        return postgrest.from(Constants.CONSENTS_SUPABASE)
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<ConsentDto>()
    }

    suspend fun deleteConsentById(id: String) {
        postgrest.from(Constants.CONSENTS_SUPABASE)
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun deleteConsentByConsultationId(consultationId: String) {
        postgrest.from(Constants.CONSENTS_SUPABASE)
            .delete {
                filter {
                    eq("consultation_id", consultationId)
                }
            }
    }
}