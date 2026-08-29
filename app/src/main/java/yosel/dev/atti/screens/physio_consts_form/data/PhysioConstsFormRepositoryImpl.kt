package yosel.dev.atti.screens.physio_consts_form.data

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PhysiologicalConstsModel
import yosel.dev.atti.core.models.model.PhysiologicalConstsWithDetailsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.room.tables.physiological_constants.PhysiologicalConstsDao
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.PhysiologicalConstsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.physio_consts_form.domain.PhysioConstsFormRepository
import javax.inject.Inject

class PhysiologicalConstsFormRepositoryImpl @Inject constructor(
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val physiologicalConstsDataSource: PhysiologicalConstsDataSource,
    private val physiologicalConstsDao: PhysiologicalConstsDao,
    private val consultationDao: ConsultationDao,
    private val consultationStepProgressDao: ConsultationStepProgressDao
) : PhysioConstsFormRepository {

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

    override suspend fun savePhysiologicalConsts(constants: PhysiologicalConstsModel): Result<PhysiologicalConstsModel> = runCatching {
        val insertedDto = physiologicalConstsDataSource.insertAndGetConstants(constants = constants.toDtoForInsert())
        physiologicalConstsDao.upsertConstants(constants = insertedDto.toEntity())

        insertedDto.id?.let { recordId ->
            consultationStepProgressDao.upsertSingleProgress(
                ConsultationStepProgressEntity(
                    consultationId = insertedDto.consultationId,
                    stepCatalogId = Constants.CONSULTATION_STEP_PHYSIOLOGICAL_CONSTS,
                    recordId = recordId,
                    isCompleted = true,
                    status = insertedDto.status
                )
            )
        }
        insertedDto.toModel()
    }

    override suspend fun updatePhysiologicalConsts(constants: PhysiologicalConstsModel): Result<PhysiologicalConstsModel> = runCatching {
        val updatedDto = physiologicalConstsDataSource.updateConstants(constants = constants.toDtoForUpdate())
        physiologicalConstsDao.upsertConstants(constants = updatedDto.toEntity())
        updatedDto.toModel()
    }

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(
            consultationId = consultationId
        ) ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getPhysiologicalConstsWithDetailsById(id: String): Result<PhysiologicalConstsWithDetailsModel> = runCatching {
        val local = physiologicalConstsDao.getConstantsWithDetailsById(id)
        if (local != null) {
            return@runCatching local.toModel()
        }
        val remoteDto = physiologicalConstsDataSource.getConstantsWithDetailsById(id)
            ?: throw NoSuchElementException("No se encontraron las constantes fisiológicas con ID: $id")

        remoteDto.weightUnit?.let { unitDto ->
            appCatalogDao.insertCatalog(unitDto.toEntity())
        }
        physiologicalConstsDao.upsertConstants(remoteDto.toEntity())
        physiologicalConstsDao.getConstantsWithDetailsById(id)?.toModel()
            ?: throw IllegalStateException("Error al recuperar las constantes guardadas localmente")
    }
}