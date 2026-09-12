package yosel.dev.atti.screens.consent_form.domain

import android.net.Uri
import yosel.dev.atti.core.models.model.ConsentModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

interface ConsentFormRepository {

    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>

    suspend fun getConsentByConsultationId(consultationId: String): Result<ConsentModel?>

    suspend fun saveImageConsent(image: Uri, consultationId: String): Result<String>

    suspend fun updateImageConsent(image: Uri, previousImageUrl: String? = null, consultationId: String): Result<String>

    suspend fun saveConsent(consent: ConsentModel): Result<ConsentModel>

    suspend fun updateConsent(consent: ConsentModel): Result<ConsentModel>
}