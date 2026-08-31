package yosel.dev.atti.screens.diagnosis_form.data

import androidx.room.withTransaction
import yosel.dev.atti.core.models.dto.DiagnosisDto
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.DiagnosisWithDetailsModel
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.room.tables.diagnosis.DiagnosisDao
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.DiagnosesDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.core.utils.toWithDetailsModel
import yosel.dev.atti.screens.diagnosis_form.domain.DiagnosisFormRepository
import javax.inject.Inject

class DiagnosisFormRepositoryImpl @Inject constructor(
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val diagnosesDataSource: DiagnosesDataSource,
    private val diagnosisDao: DiagnosisDao,
    private val consultationDao: ConsultationDao,
    private val appDatabase: AppDatabase,
    private val consultationStepProgressDao: ConsultationStepProgressDao
) : DiagnosisFormRepository {

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

    override suspend fun saveDiagnoses(
        consultationId: String,
        selectedCatalogs: List<AppCatalogModel>
    ): Result<List<DiagnosisWithDetailsModel>> = runCatching {
        val diagnosesDtos = selectedCatalogs.map { catalog ->
            DiagnosisDto(
                consultationId = consultationId,
                diagnosisCatalogId = catalog.id,
                status = Constants.ACTIVE_STATUS
            )
        }

        // Inserción directa en la tabla de Supabase
        val insertedDtos = diagnosesDataSource.insertDiagnoses(diagnosesDtos)

        // Sincronización en Room
        val entities = insertedDtos.map { it.toEntity() }
        diagnosisDao.syncDiagnosesForConsultation(
            consultationId = consultationId,
            diagnoses = entities
        )

        // Actualizar progreso del paso de la consulta
        consultationStepProgressDao.upsertSingleProgress(
            ConsultationStepProgressEntity(
                consultationId = consultationId,
                stepCatalogId = Constants.CONSULTATION_STEP_DIAGNOSIS,
                recordId = insertedDtos.firstOrNull()?.id,
                isCompleted = true,
                status = Constants.ACTIVE_STATUS
            )
        )

        insertedDtos.map { it.toWithDetailsModel() }
    }

    override suspend fun updateDiagnoses(
        consultationId: String,
        selectedCatalogs: List<AppCatalogModel>
    ): Result<List<DiagnosisWithDetailsModel>> = runCatching {
        val diagnosesDtos = selectedCatalogs.map { catalog ->
            DiagnosisDto(
                consultationId = consultationId,
                diagnosisCatalogId = catalog.id,
                status = Constants.ACTIVE_STATUS
            )
        }

        // En Supabase: eliminamos los existentes para la consulta e insertamos los nuevos
        diagnosesDataSource.deleteDiagnosesByConsultationId(consultationId)
        val insertedDtos = diagnosesDataSource.insertDiagnoses(diagnosesDtos)

        // En Room: sincronizamos atómicamente
        appDatabase.withTransaction {
            val entities = insertedDtos.map { it.toEntity() }
            diagnosisDao.syncDiagnosesForConsultation(
                consultationId = consultationId,
                diagnoses = entities
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

    override suspend fun getDiagnosesByConsultationId(consultationId: String): Result<List<DiagnosisWithDetailsModel>> = runCatching {
        val localDiagnoses = diagnosisDao.getDiagnosesWithDetailsByConsultationId(consultationId)
        if (localDiagnoses.isNotEmpty()) {
            return@runCatching localDiagnoses.map { it.toModel() }
        }

        val remoteDtos = diagnosesDataSource.getDiagnosesWithDetailsByConsultationId(consultationId)
        val catalogsToInsert = remoteDtos.mapNotNull { it.catalog?.toEntity() }.distinctBy { it.id }
        if (catalogsToInsert.isNotEmpty()) {
            appCatalogDao.insertAllCatalogs(catalogsToInsert)
        }

        val entities = remoteDtos.map { it.toEntity() }
        diagnosisDao.syncDiagnosesForConsultation(consultationId, entities)

        diagnosisDao.getDiagnosesWithDetailsByConsultationId(consultationId).map { it.toModel() }
    }
}