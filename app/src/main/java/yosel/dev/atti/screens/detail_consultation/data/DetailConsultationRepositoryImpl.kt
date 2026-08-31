package yosel.dev.atti.screens.detail_consultation.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.dto.StepReferenceDto
import yosel.dev.atti.core.models.model.ConsultationStepProgressModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.room.tables.anamnesis.AnamnesisDao
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressDao
import yosel.dev.atti.core.room.tables.consultation_step_progress.ConsultationStepProgressEntity
import yosel.dev.atti.core.room.tables.consultation_type_step.ConsultationTypeStepDao
import yosel.dev.atti.core.supabase.AnamnesisDataSource
import yosel.dev.atti.core.supabase.ConsultationTypeStepsDataSource
import yosel.dev.atti.core.supabase.ConsultationsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.detail_consultation.domain.DetailConsultationRepository
import javax.inject.Inject

class DetailConsultationRepositoryImpl @Inject constructor(
    private val consultationDao: ConsultationDao,
    private val consultationTypeStepDao: ConsultationTypeStepDao,
    private val consultationTypeStepsDataSource: ConsultationTypeStepsDataSource,
    private val consultationsDataSource: ConsultationsDataSource,
    private val consultationStepProgressDao: ConsultationStepProgressDao,
    private val appCatalogDao: AppCatalogDao
) : DetailConsultationRepository {

    override fun getConsultationWithDetailsFlow(consultationId: String): Flow<ConsultationWithDetailsModel?> {
        return consultationDao.getConsultationWithDetailsByIdFlow(consultationId = consultationId)
            .map { entity -> entity?.toModel() }
            .flowOn(Dispatchers.IO)
    }

    override fun getConsultationStepsProgressFlow(
        consultationId: String,
        consultationTypeId: Int
    ): Flow<List<ConsultationStepProgressModel>> {
        val stepsFlow = consultationTypeStepDao.getStepsWithDetailsByConsultationTypeIdFlow(consultationTypeId)
        val progressFlow = consultationStepProgressDao.getProgressByConsultationIdFlow(consultationId)

        return combine(stepsFlow, progressFlow) { stepsEntities, progressEntities ->
            val progressMap = progressEntities.associateBy { it.stepCatalogId }

            stepsEntities.map { stepWithDetailEntity ->
                val stepModel = stepWithDetailEntity.toModel()
                val progress = progressMap[stepModel.stepCatalog.id]

                ConsultationStepProgressModel(
                    typeStep = stepModel.typeStep,
                    stepCatalog = stepModel.stepCatalog,
                    isCompleted = progress?.isCompleted == true,
                    recordId = progress?.recordId
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun syncConsultationSteps(
        consultationId: String,
        consultationTypeId: Int
    ): Result<Unit> = runCatching {
        // 1. Sincronizar catálogo de pasos
        val remoteSteps = consultationTypeStepsDataSource.getStepsByConsultationTypeId(consultationTypeId)
        val allCatalogs = remoteSteps.flatMap { step ->
            listOfNotNull(step.consultationType?.toEntity(), step.stepCatalog?.toEntity())
        }.distinctBy { it.id }
        appCatalogDao.insertAllCatalogs(allCatalogs)
        consultationTypeStepDao.upsertSteps(remoteSteps.map { it.toEntity() })

        // 2. Traer ÚNICAMENTE la metadata ligera desde Supabase
        val progressDto = consultationsDataSource.getConsultationProgressById(consultationId) ?: return@runCatching

        // 3. Mapear cada paso ligero a la tabla de progreso sin consultar datos pesados
        val progressEntities = mutableListOf<ConsultationStepProgressEntity>()

        // Mapeo Anamnesis
        val remoteAnamnesis = progressDto.anamnesis.firstOrNull { it.status != Constants.DELETED_STATUS }
        val anamnesisCatalogId = remoteSteps.firstOrNull {
            it.stepCatalog?.name?.contains("anamnesis", ignoreCase = true) == true
        }?.stepCatalogId

        if (anamnesisCatalogId != null) {
            progressEntities.add(
                ConsultationStepProgressEntity(
                    consultationId = consultationId,
                    stepCatalogId = anamnesisCatalogId,
                    recordId = remoteAnamnesis?.id,
                    isCompleted = remoteAnamnesis != null && !remoteAnamnesis.id.isNullOrBlank(),
                    status = remoteAnamnesis?.status ?: Constants.ACTIVE_STATUS
                )
            )
        }

        //Mapeo Clinical Examinations
        val remoteClinicalExaminations = progressDto.clinicalExaminations.firstOrNull { it.status != Constants.DELETED_STATUS }
        val clinicalExaminationsCatalogId = remoteSteps.firstOrNull {
            it.stepCatalog?.name?.contains("examen clínico", ignoreCase = true) == true
        }?.stepCatalogId

        if (clinicalExaminationsCatalogId != null){
            progressEntities.add(
                ConsultationStepProgressEntity(
                    consultationId = consultationId,
                    stepCatalogId = clinicalExaminationsCatalogId,
                    recordId = remoteClinicalExaminations?.id,
                    isCompleted = remoteClinicalExaminations != null && !remoteClinicalExaminations.id.isNullOrBlank(),
                    status = remoteClinicalExaminations?.status ?: Constants.ACTIVE_STATUS
                )
            )
        }

        //Mapeo Constantes Fisiologicas
        val remotePhysicalConstants = progressDto.physiologicalConstants.firstOrNull { it.status != Constants.DELETED_STATUS }
        val physicalConstantsCatalogId = remoteSteps.firstOrNull {
            it.stepCatalog?.name?.contains("constantes fisiológicas", ignoreCase = true) == true
        }?.stepCatalogId

        if (physicalConstantsCatalogId != null){
            progressEntities.add(
                ConsultationStepProgressEntity(
                    consultationId = consultationId,
                    stepCatalogId = physicalConstantsCatalogId,
                    recordId = remotePhysicalConstants?.id,
                    isCompleted = remotePhysicalConstants != null && !remotePhysicalConstants.id.isNullOrBlank(),
                    status = remotePhysicalConstants?.status ?: Constants.ACTIVE_STATUS
                )
            )
        }

        //Mapeo Diagnóstico
        val remoteDiagnoses = progressDto.diagnoses.firstOrNull { it.status != Constants.DELETED_STATUS }
        val diagnosesCatalogId = remoteSteps.firstOrNull {
            it.stepCatalog?.name?.contains("diagnóstico", ignoreCase = true) == true
        }?.stepCatalogId

        if (diagnosesCatalogId != null){
            progressEntities.add(
                ConsultationStepProgressEntity(
                    consultationId = consultationId,
                    stepCatalogId = diagnosesCatalogId,
                    recordId = remoteDiagnoses?.id,
                    isCompleted = remoteDiagnoses != null && !remoteDiagnoses.id.isNullOrBlank(),
                    status = remoteDiagnoses?.status ?: Constants.ACTIVE_STATUS
                )
            )
        }

        // Aquí se agregarán los mapeos de futuros pasos (examen físico, diagnóstico, etc.)
        consultationStepProgressDao.upsertProgress(progressEntities)
    }
}