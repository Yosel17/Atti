package yosel.dev.atti.screens.observation_form.data

import androidx.room.withTransaction
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.ObservationModel
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.room.tables.observation.ObservationDao
import yosel.dev.atti.core.supabase.ObservationsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.observation_form.domain.ObservationFormRepository
import javax.inject.Inject

class ObservationFormRepositoryImpl @Inject constructor(
    private val observationsDataSource: ObservationsDataSource,
    private val observationDao: ObservationDao,
    private val consultationDao: ConsultationDao,
    private val appDatabase: AppDatabase,
    private val consultationStepProgressDao: ConsultationStepProgressDao,
) : ObservationFormRepository {

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(consultationId)
            ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getObservationByConsultationId(consultationId: String): Result<ObservationModel?> = runCatching {
        val localObservation = observationDao.getObservationByConsultationId(consultationId)
        if (localObservation != null) {
            return@runCatching localObservation.toModel()
        }
        val remoteDto = observationsDataSource.getObservationByConsultationId(consultationId) ?: return@runCatching null
        observationDao.upsertObservation(remoteDto.toEntity())
        remoteDto.toModel()
    }

    override suspend fun saveObservation(observation: ObservationModel): Result<ObservationModel> = runCatching {
        val insertedDto = observationsDataSource.insertAndGetObservation(observation.toDtoForInsert())
        appDatabase.withTransaction {
            observationDao.upsertObservation(insertedDto.toEntity())
            consultationStepProgressDao.upsertSingleProgress(
                ConsultationStepProgressEntity(
                    consultationId = observation.consultationId,
                    stepCatalogId = Constants.OBSERVATION_STEP_DIAGNOSIS,
                    recordId = insertedDto.id,
                    isCompleted = true,
                    status = Constants.ACTIVE_STATUS
                )
            )
        }
        insertedDto.toModel()
    }

    override suspend fun updateObservation(observation: ObservationModel): Result<ObservationModel> = runCatching {
        val updatedDto = observationsDataSource.updateObservation(observation.toDtoForUpdate())
        observationDao.upsertObservation(updatedDto.toEntity())
        updatedDto.toModel()
    }
}