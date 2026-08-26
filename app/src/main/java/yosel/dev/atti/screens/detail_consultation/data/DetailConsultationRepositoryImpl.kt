package yosel.dev.atti.screens.detail_consultation.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.ConsultationStepProgressModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.room.tables.anamnesis.AnamnesisDao
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_type_step.ConsultationTypeStepDao
import yosel.dev.atti.core.supabase.AnamnesisDataSource
import yosel.dev.atti.core.supabase.ConsultationTypeStepsDataSource
import yosel.dev.atti.core.supabase.ConsultationsDataSource
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
    private val anamnesisDao: AnamnesisDao,
    private val anamnesisDataSource: AnamnesisDataSource,
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
        val anamnesisFlow = anamnesisDao.getAnamnesisByConsultationIdFlow(consultationId)

        return combine(stepsFlow, anamnesisFlow) { stepsEntities, anamnesisEntity ->
            stepsEntities.map { stepWithDetailEntity ->
                val stepModel = stepWithDetailEntity.toModel()
                val normalizedStepName = stepModel.stepCatalog.name.normalize()

                val (isCompleted, recordId) = when {
                    normalizedStepName.contains("anamnesis") && anamnesisEntity != null -> {
                        true to anamnesisEntity.id
                    }
                    else -> false to null
                }

                ConsultationStepProgressModel(
                    typeStep = stepModel.typeStep,
                    stepCatalog = stepModel.stepCatalog,
                    isCompleted = isCompleted,
                    recordId = recordId
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

        // 2. Consulta anidada única para comprobar progreso en Supabase
        val progress = consultationsDataSource.getConsultationProgressById(consultationId)
        val remoteAnamnesis = progress?.anamnesis?.firstOrNull()

        // 3. Si existe anamnesis en Supabase pero no localmente en Room, se sincroniza con detalle
        if (remoteAnamnesis != null && remoteAnamnesis.id != null) {
            val localAnamnesis = anamnesisDao.getAnamnesisByConsultationId(consultationId)
            if (localAnamnesis == null) {
                anamnesisDataSource.getAnamnesisWithDetailsByConsultationId(consultationId)?.let { detailedDto ->
                    anamnesisDao.saveAnamnesisWithDetails(
                        anamnesis = detailedDto.toEntity(),
                        options = detailedDto.environmentOptions.map { it.toEntity() },
                        vaccines = detailedDto.vaccines.map { it.toEntity() },
                        dewormings = detailedDto.dewormings.map { it.toEntity() }
                    )
                }
            }
        }
    }
}