package yosel.dev.atti.screens.auxiliary_test_form.data

import androidx.room.withTransaction
import yosel.dev.atti.core.models.dto.AuxiliaryTestDto
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.AuxiliaryTestWithDetailsModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.auxiliary_test.AuxiliaryTestDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.AuxiliaryTestsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.core.utils.toWithDetailsModel
import yosel.dev.atti.screens.auxiliary_test_form.domain.AuxiliaryTestFormRepository
import javax.inject.Inject

class AuxiliaryTestFormRepositoryImpl @Inject constructor(
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val auxiliaryTestsDataSource: AuxiliaryTestsDataSource,
    private val auxiliaryTestDao: AuxiliaryTestDao,
    private val consultationDao: ConsultationDao,
    private val appDatabase: AppDatabase,
    private val consultationStepProgressDao: ConsultationStepProgressDao
) : AuxiliaryTestFormRepository {

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

    override suspend fun saveAuxiliaryTests(
        consultationId: String,
        selectedCatalogs: List<AppCatalogModel>
    ): Result<List<AuxiliaryTestWithDetailsModel>> = runCatching {
        val auxiliaryTestsDtos = selectedCatalogs.map { catalog ->
            AuxiliaryTestDto(
                consultationId = consultationId,
                testCatalogId = catalog.id,
                status = Constants.ACTIVE_STATUS
            )
        }

        val insertedDtos = auxiliaryTestsDataSource.insertAuxiliaryTests(auxiliaryTestsDtos)

        val entities = insertedDtos.map { it.toEntity() }
        auxiliaryTestDao.syncAuxiliaryTestsForConsultation(
            consultationId = consultationId,
            tests = entities
        )

        consultationStepProgressDao.upsertSingleProgress(
            ConsultationStepProgressEntity(
                consultationId = consultationId,
                stepCatalogId = Constants.CONSULTATION_STEP_AUXILIARY_TEST,
                recordId = insertedDtos.firstOrNull()?.id,
                isCompleted = true,
                status = Constants.ACTIVE_STATUS
            )
        )

        insertedDtos.map { it.toWithDetailsModel() }
    }

    override suspend fun updateAuxiliaryTests(
        consultationId: String,
        selectedCatalogs: List<AppCatalogModel>
    ): Result<List<AuxiliaryTestWithDetailsModel>> = runCatching {
        val auxiliaryTestsDtos = selectedCatalogs.map { catalog ->
            AuxiliaryTestDto(
                consultationId = consultationId,
                testCatalogId = catalog.id,
                status = Constants.ACTIVE_STATUS
            )
        }

        auxiliaryTestsDataSource.deleteAuxiliaryTestsByConsultationId(consultationId)
        val insertedDtos = auxiliaryTestsDataSource.insertAuxiliaryTests(auxiliaryTestsDtos)

        appDatabase.withTransaction {
            val entities = insertedDtos.map { it.toEntity() }
            auxiliaryTestDao.syncAuxiliaryTestsForConsultation(
                consultationId = consultationId,
                tests = entities
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

    override suspend fun getAuxiliaryTestsByConsultationId(consultationId: String): Result<List<AuxiliaryTestWithDetailsModel>> = runCatching {
        val localTests = auxiliaryTestDao.getAuxiliaryTestsWithDetailsByConsultationId(consultationId)
        if (localTests.isNotEmpty()) {
            return@runCatching localTests.map { it.toModel() }
        }

        val remoteDtos = auxiliaryTestsDataSource.getAuxiliaryTestsWithDetailsByConsultationId(consultationId)
        val catalogsToInsert = remoteDtos.mapNotNull { it.catalog?.toEntity() }.distinctBy { it.id }
        if (catalogsToInsert.isNotEmpty()) {
            appCatalogDao.insertAllCatalogs(catalogsToInsert)
        }

        val entities = remoteDtos.map { it.toEntity() }
        auxiliaryTestDao.syncAuxiliaryTestsForConsultation(consultationId, entities)

        auxiliaryTestDao.getAuxiliaryTestsWithDetailsByConsultationId(consultationId).map { it.toModel() }
    }
}
