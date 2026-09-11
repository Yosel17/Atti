package yosel.dev.atti.screens.consent_form.data

import android.net.Uri
import androidx.room.withTransaction
import yosel.dev.atti.core.models.model.ConsentModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.consent.ConsentDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.supabase.ConsentsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.consent_form.domain.ConsentFormRepository
import javax.inject.Inject

class ConsentFormRepositoryImpl @Inject constructor(
    private val consultationDao: ConsultationDao,
    private val consentDao: ConsentDao,
    private val consentsDataSource: ConsentsDataSource,
    private val appDatabase: AppDatabase,
    private val consultationStepProgressDao: ConsultationStepProgressDao,
): ConsentFormRepository{

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(consultationId)
            ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getConsentByConsultationId(consultationId: String): Result<ConsentModel?> = runCatching {
        val localConsent = consentDao.getConsentByConsultationId(consultationId)
        if (localConsent != null){
            return@runCatching localConsent.toModel()
        }
        val remoteDto = consentsDataSource.getConsentByConsultationId(consultationId) ?: return@runCatching null
        consentDao.upsertConsent(remoteDto.toEntity())
        remoteDto.toModel()
    }

    override suspend fun saveImageConsent(image: Uri): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun saveConsent(consent: ConsentModel): Result<ConsentModel> = runCatching {
        val insertdDto = consentsDataSource.insertAndGetConsent(consent.toDtoForInsert())
        appDatabase.withTransaction {
            consentDao.upsertConsent(insertdDto.toEntity())
            consultationStepProgressDao.upsertSingleProgress(
                ConsultationStepProgressEntity(
                    consultationId = consent.consultationId,
                    stepCatalogId = Constants.CONSULTATION_STEP_CONSENT,
                    recordId = insertdDto.id,
                    isCompleted = true,
                    status = Constants.ACTIVE_STATUS
                )
            )
        }
        insertdDto.toModel()
    }

    override suspend fun updateConsent(consent: ConsentModel): Result<ConsentModel> = runCatching {
        val updatedDto = consentsDataSource.updateConsent(consent.toDtoForUpdate())
        consentDao.upsertConsent(updatedDto.toEntity())
        updatedDto.toModel()
    }
}