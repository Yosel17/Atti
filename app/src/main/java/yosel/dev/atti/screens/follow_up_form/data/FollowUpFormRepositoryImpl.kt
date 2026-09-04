package yosel.dev.atti.screens.follow_up_form.data

import androidx.room.withTransaction
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.FollowUpModel
import yosel.dev.atti.core.models.model.FollowUpWithDetailsModel
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.room.tables.follow_up.FollowUpDao
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.FollowUpsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.core.utils.toWithDetailsModel
import yosel.dev.atti.screens.follow_up_form.domain.FollowUpFormRepository
import javax.inject.Inject

class FollowUpFormRepositoryImpl @Inject constructor(
    private val followUpsDataSource: FollowUpsDataSource,
    private val followUpDao: FollowUpDao,
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val consultationDao: ConsultationDao,
    private val consultationStepProgressDao: ConsultationStepProgressDao,
    private val appDatabase: AppDatabase
) : FollowUpFormRepository {

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(consultationId)
            ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getFollowUpById(followUpId: String): Result<FollowUpWithDetailsModel?> = runCatching {
        val local = followUpDao.getFollowUpWithDetailsById(followUpId)
        if (local != null) {
            return@runCatching local.toModel()
        }
        val remoteDto = followUpsDataSource.getFollowUpWithDetailsById(followUpId) ?: return@runCatching null
        followUpDao.upsertFollowUp(remoteDto.toEntity())
        remoteDto.toWithDetailsModel()
    }

    override suspend fun getFollowUpByConsultationId(consultationId: String): Result<FollowUpWithDetailsModel?> = runCatching {
        val local = followUpDao.getFollowUpWithDetailsByConsultationId(consultationId)
        if (local != null) {
            return@runCatching local.toModel()
        }
        val remoteList = followUpsDataSource.getFollowUpsWithDetailsByConsultationId(consultationId)
        val firstDto = remoteList.firstOrNull() ?: return@runCatching null
        followUpDao.upsertFollowUp(firstDto.toEntity())
        firstDto.toWithDetailsModel()
    }

    override suspend fun getQuickReasonCatalogs(): Result<List<AppCatalogModel>> = runCatching {
        val remoteCatalogs = appCatalogsDataSource.getCatalogsByTypes(listOf(Constants.QUICK_REASONS_CATALOG_TYPE))
        val entities = remoteCatalogs.map { it.toEntity() }
        appCatalogDao.insertAllCatalogs(entities)
        remoteCatalogs.map { it.toModel() }
    }

    override suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel> = runCatching {
        val appCatalogDto = appCatalogsDataSource.insertAndGetCatalog(catalog = catalog.toDtoForInsert())
        appCatalogDao.insertCatalog(catalog = appCatalogDto.toEntity())
        appCatalogDto.toModel()
    }

    override suspend fun saveFollowUp(
        consultationId: String,
        followUp: FollowUpModel
    ): Result<FollowUpWithDetailsModel> = runCatching {
        val insertedDto = followUpsDataSource.insertAndGetFollowUp(followUp.toDtoForInsert())
        appDatabase.withTransaction {
            followUpDao.upsertFollowUp(insertedDto.toEntity())
            consultationStepProgressDao.upsertSingleProgress(
                ConsultationStepProgressEntity(
                    consultationId = consultationId,
                    stepCatalogId = Constants.FOLLOW_UP_STEP_DIAGNOSIS,
                    recordId = insertedDto.id,
                    isCompleted = true,
                    status = Constants.ACTIVE_STATUS
                )
            )
        }
        insertedDto.toWithDetailsModel()
    }

    override suspend fun updateFollowUp(
        consultationId: String,
        followUp: FollowUpModel
    ): Result<FollowUpWithDetailsModel> = runCatching {
        val updatedDto = followUpsDataSource.updateFollowUp(followUp.toDtoForUpdate())
        followUpDao.upsertFollowUp(updatedDto.toEntity())
        updatedDto.toWithDetailsModel()
    }
}