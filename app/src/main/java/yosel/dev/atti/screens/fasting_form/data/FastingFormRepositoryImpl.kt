package yosel.dev.atti.screens.fasting_form.data

import yosel.dev.atti.core.models.dto.FastingDto
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.FastingWithDetailsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.room.tables.fasting.FastingDao
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.FastingDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.core.utils.toWithDetailsModel
import yosel.dev.atti.screens.fasting_form.domain.FastingFormRepository
import javax.inject.Inject

class FastingFormRepositoryImpl @Inject constructor(
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val fastingDataSource: FastingDataSource,
    private val fastingDao: FastingDao,
    private val consultationDao: ConsultationDao,
    private val consultationStepProgressDao: ConsultationStepProgressDao
) : FastingFormRepository {

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

    override suspend fun saveFasting(
        consultationId: String,
        foodFastingId: Int,
        waterFastingId: Int
    ): Result<FastingWithDetailsModel> = runCatching {
        val fastingDto = FastingDto(
            consultationId = consultationId,
            foodFastingCatalogId = foodFastingId,
            waterFastingCatalogId = waterFastingId,
            status = Constants.ACTIVE_STATUS
        )

        val insertedDto = fastingDataSource.insertAndGetFasting(fastingDto)

        fastingDao.upsertFasting(insertedDto.toEntity())

        consultationStepProgressDao.upsertSingleProgress(
            ConsultationStepProgressEntity(
                consultationId = consultationId,
                stepCatalogId = Constants.CONSULTATION_STEP_FASTING,
                recordId = insertedDto.id,
                isCompleted = true,
                status = Constants.ACTIVE_STATUS
            )
        )

        insertedDto.toWithDetailsModel()
    }

    override suspend fun updateFasting(
        id: String,
        consultationId: String,
        foodFastingId: Int,
        waterFastingId: Int
    ): Result<FastingWithDetailsModel> = runCatching {
        val fastingDto = FastingDto(
            id = id,
            consultationId = consultationId,
            foodFastingCatalogId = foodFastingId,
            waterFastingCatalogId = waterFastingId,
            status = Constants.ACTIVE_STATUS
        )

        val updatedDto = fastingDataSource.updateFasting(fastingDto)

        fastingDao.upsertFasting(updatedDto.toEntity())

        updatedDto.toWithDetailsModel()
    }

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(
            consultationId = consultationId
        ) ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getFastingByConsultationId(consultationId: String): Result<FastingWithDetailsModel?> = runCatching {
        val localFasting = fastingDao.getFastingWithDetailsByConsultationId(consultationId)
        if (localFasting != null) {
            return@runCatching localFasting.toModel()
        }

        val remoteDto = fastingDataSource.getFastingWithDetailsByConsultationId(consultationId)
        if (remoteDto != null) {
            val catalogsToInsert = buildList {
                remoteDto.foodFasting?.let { add(it.toEntity()) }
                remoteDto.waterFasting?.let { add(it.toEntity()) }
            }.distinctBy { it.id }
            if (catalogsToInsert.isNotEmpty()) {
                appCatalogDao.insertAllCatalogs(catalogsToInsert)
            }
            
            fastingDao.upsertFasting(remoteDto.toEntity())
            return@runCatching remoteDto.toWithDetailsModel()
        }

        null
    }
}