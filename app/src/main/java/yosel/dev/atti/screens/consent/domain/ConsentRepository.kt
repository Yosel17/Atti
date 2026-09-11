package yosel.dev.atti.screens.consent.domain

import android.net.Uri
import yosel.dev.atti.core.models.model.ConsentModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

interface ConsentRepository {

    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>

    suspend fun getConsentByConsultationId(consultationId: String): Result<ConsentModel?>

    suspend fun saveImageConsent(image: Uri): Result<String>

    suspend fun saveConsent(consent: ConsentModel): Result<ConsentModel>

    suspend fun updateConsent(consent: ConsentModel): Result<ConsentModel>
}