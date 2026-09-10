package yosel.dev.atti.screens.asa_classification_form.data

import androidx.room.withTransaction
import yosel.dev.atti.core.models.dto.AsaClassificationDto
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.AsaClassificationWithDetailsModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.asa_classification.AsaClassificationDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.AsaClassificationsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.core.utils.toWithDetailsModel
import yosel.dev.atti.screens.asa_classification_form.domain.AsaClassificationFormRepository
import javax.inject.Inject

class AsaClassificationFormRepositoryImpl @Inject constructor(
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val asaClassificationsDataSource: AsaClassificationsDataSource,
    private val asaClassificationDao: AsaClassificationDao,
    private val consultationDao: ConsultationDao,
    private val appDatabase: AppDatabase,
    private val consultationStepProgressDao: ConsultationStepProgressDao
) : AsaClassificationFormRepository {

    override suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>> = runCatching {
        val remoteAppCatalogs = appCatalogsDataSource.getCatalogsByTypes(types = types)
        val entities = remoteAppCatalogs.map { it.toEntity() }
        appCatalogDao.insertAllCatalogs(catalogs = entities)
        remoteAppCatalogs.map { it.toModel() }
    }

    override suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel> = runCatching {
        val appCatalogDto = appCatalogsDataSource.insertAndGetCatalog(catalog = catalog.toDtoForInsert())
        appCatalogDao.insertCatalog(catalog = appCatalogDto.toEntity())
        appCatalogDto.toModel()
    }

    override suspend fun saveAsaClassifications(
        consultationId: String,
        selectedCatalogs: List<AppCatalogModel>
    ): Result<List<AsaClassificationWithDetailsModel>> = runCatching {
        val asaClassificationsDtos = selectedCatalogs.map { catalog ->
            AsaClassificationDto(
                consultationId = consultationId,
                asaCatalogId = catalog.id,
                status = Constants.ACTIVE_STATUS
            )
        }

        val insertedDtos = asaClassificationsDataSource.insertAsaClassifications(asaClassificationsDtos)

        val entities = insertedDtos.map { it.toEntity() }
        asaClassificationDao.syncAsaClassificationsForConsultation(
            consultationId = consultationId,
            classifications = entities
        )

        consultationStepProgressDao.upsertSingleProgress(
            ConsultationStepProgressEntity(
                consultationId = consultationId,
                stepCatalogId = Constants.CONSULTATION_STEP_ASA_CLASSIFICATION,
                recordId = insertedDtos.firstOrNull()?.id,
                isCompleted = true,
                status = Constants.ACTIVE_STATUS
            )
        )

        insertedDtos.map { it.toWithDetailsModel() }
    }

    override suspend fun updateAsaClassifications(
        consultationId: String,
        selectedCatalogs: List<AppCatalogModel>
    ): Result<List<AsaClassificationWithDetailsModel>> = runCatching {
        val asaClassificationsDtos = selectedCatalogs.map { catalog ->
            AsaClassificationDto(
                consultationId = consultationId,
                asaCatalogId = catalog.id,
                status = Constants.ACTIVE_STATUS
            )
        }

        asaClassificationsDataSource.deleteAsaClassificationsByConsultationId(consultationId)
        val insertedDtos = asaClassificationsDataSource.insertAsaClassifications(asaClassificationsDtos)

        appDatabase.withTransaction {
            val entities = insertedDtos.map { it.toEntity() }
            asaClassificationDao.syncAsaClassificationsForConsultation(
                consultationId = consultationId,
                classifications = entities
            )
        }

        insertedDtos.map { it.toWithDetailsModel() }
    }

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(
            consultationId = consultationId
        ) ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getAsaClassificationsByConsultationId(consultationId: String): Result<List<AsaClassificationWithDetailsModel>> = runCatching {
        val localClassifications = asaClassificationDao.getAsaClassificationsWithDetailsByConsultationId(consultationId)
        if (localClassifications.isNotEmpty()) {
            return@runCatching localClassifications.map { it.toModel() }
        }

        val remoteDtos = asaClassificationsDataSource.getAsaClassificationsWithDetailsByConsultationId(consultationId)
        val catalogsToInsert = remoteDtos.mapNotNull { it.catalog?.toEntity() }.distinctBy { it.id }
        if (catalogsToInsert.isNotEmpty()) {
            appCatalogDao.insertAllCatalogs(catalogsToInsert)
        }

        val entities = remoteDtos.map { it.toEntity() }
        asaClassificationDao.syncAsaClassificationsForConsultation(consultationId, entities)

        asaClassificationDao.getAsaClassificationsWithDetailsByConsultationId(consultationId).map { it.toModel() }
    }
}
