package yosel.dev.atti.screens.clinical_exam_form.data

import androidx.room.withTransaction
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClinicalExamLymphNodeModel
import yosel.dev.atti.core.models.model.ClinicalExamWithDetailsModel
import yosel.dev.atti.core.models.model.ClinicalExaminationModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.request.CreateClinicalExamRequest
import yosel.dev.atti.core.models.request.UpdateClinicalExamRequest
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.clinical_examination.ClinicalExaminationDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.ClinicalExaminationDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.clinical_exam_form.domain.ClinicalExamFormRepository
import javax.inject.Inject

class ClinicalExamFormRepositoryImpl @Inject constructor(
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val clinicalExaminationDataSource: ClinicalExaminationDataSource,
    private val clinicalExaminationDao: ClinicalExaminationDao,
    private val consultationDao: ConsultationDao,
    private val appDatabase: AppDatabase,
    private val consultationStepProgressDao: ConsultationStepProgressDao
) : ClinicalExamFormRepository {

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

    override suspend fun saveClinicalExam(
        clinicalExam: ClinicalExaminationModel,
        lymphNodes: List<ClinicalExamLymphNodeModel>
    ): Result<ClinicalExaminationModel> = runCatching {
        val request = CreateClinicalExamRequest(
            clinicalExamData = clinicalExam.toDtoForInsert(),
            lymphNodesData = lymphNodes.map { it.toDtoForInsert() }
        )
        val insertedExamDto = clinicalExaminationDataSource.insertClinicalExamWithDetails(request = request)
        clinicalExaminationDao.saveClinicalExamWithDetails(
            exam = insertedExamDto.toEntity(),
            lymphNodes = insertedExamDto.lymphNodes.map { it.toEntity() }
        )
        consultationStepProgressDao.upsertSingleProgress(
            ConsultationStepProgressEntity(
                consultationId = insertedExamDto.consultationId,
                stepCatalogId = Constants.CONSULTATION_STEP_CLINICAL_EXAM,
                recordId = insertedExamDto.id,
                isCompleted = true,
                status = insertedExamDto.status
            )
        )
        insertedExamDto.toModel()
    }

    override suspend fun updateClinicalExamWithDetails(
        clinicalExam: ClinicalExaminationModel,
        lymphNodes: List<ClinicalExamLymphNodeModel>?
    ): Result<Unit> = runCatching {
        val request = UpdateClinicalExamRequest(
            clinicalExamData = clinicalExam.toDtoForUpdate(),
            lymphNodesData = lymphNodes?.map { it.toDtoForInsert() }
        )
        val updatedExamDto = clinicalExaminationDataSource.updateClinicalExamWithDetails(request)
        appDatabase.withTransaction {
            clinicalExaminationDao.upsertClinicalExam(updatedExamDto.toEntity())
            if (lymphNodes != null) {
                clinicalExaminationDao.deleteLymphNodesByExamId(clinicalExam.id)
                if (updatedExamDto.lymphNodes.isNotEmpty()) {
                    clinicalExaminationDao.upsertLymphNodes(
                        updatedExamDto.lymphNodes.map { it.toEntity() }
                    )
                }
            }
        }
    }

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(
            consultationId = consultationId
        ) ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getClinicalExamWithDetailsById(examId: String): Result<ClinicalExamWithDetailsModel> = runCatching {
        val localExam = clinicalExaminationDao.getClinicalExamWithDetailsById(examId)
        if (localExam != null) {
            return@runCatching localExam.toModel()
        }
        val remoteDto = clinicalExaminationDataSource.getClinicalExamWithDetailsById(examId)
            ?: throw NoSuchElementException("No se encontró el examen clínico con ID: $examId")
        val catalogsToInsert = buildList {
            remoteDto.coat?.let { add(it.toEntity()) }
            remoteDto.lymphNodes.forEach { node ->
                node.catalog?.let { add(it.toEntity()) }
            }
        }.distinctBy { it.id }
        if (catalogsToInsert.isNotEmpty()) {
            appCatalogDao.insertAllCatalogs(catalogsToInsert)
        }
        clinicalExaminationDao.saveClinicalExamWithDetails(
            exam = remoteDto.toEntity(),
            lymphNodes = remoteDto.lymphNodes.map { it.toEntity() }
        )
        clinicalExaminationDao.getClinicalExamWithDetailsById(examId)?.toModel()
            ?: throw IllegalStateException("Error al recuperar el examen clínico guardado localmente")
    }
}