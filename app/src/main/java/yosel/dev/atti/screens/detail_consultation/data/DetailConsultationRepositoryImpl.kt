package yosel.dev.atti.screens.detail_consultation.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationTypeStepWithDetailsModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.consultation_type_step.ConsultationTypeStepDao
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.ConsultationTypeStepsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.detail_consultation.domain.DetailConsultationRepository
import javax.inject.Inject
import kotlin.collections.map

class DetailConsultationRepositoryImpl @Inject constructor(
    private val consultationDao: ConsultationDao,
    private val consultationTypeStepDao: ConsultationTypeStepDao,
    private val consultationTypeStepsDataSource: ConsultationTypeStepsDataSource,
    private val appCatalogDao: AppCatalogDao,
): DetailConsultationRepository {

    override fun getConsultationWithDetailsFlow(consultationId: String): Flow<ConsultationWithDetailsModel?> {
        return consultationDao.getConsultationWithDetailsByIdFlow(consultationId = consultationId)
            .map { entity -> entity?.toModel() }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getConsultationSteps(): Result<List<ConsultationTypeStepWithDetailsModel>> = runCatching {
        val remoteConsultationTypeSteps = consultationTypeStepsDataSource.getStepsByConsultationTypeId(Constants.GENERAL_CONSULTATION_TYPE)
        val allCatalogEntities = remoteConsultationTypeSteps.flatMap { consultationTypeStep ->
            listOfNotNull(
                consultationTypeStep.consultationType?.toEntity(),
                consultationTypeStep.stepCatalog?.toEntity(),
            )
        }.distinctBy { it.id }
        val consultationTypeStepsEntities = remoteConsultationTypeSteps.map { it.toEntity() }
        appCatalogDao.insertAllCatalogs(allCatalogEntities)
        consultationTypeStepDao.upsertSteps(consultationTypeStepsEntities)
        val localConsultationTypeSteps = consultationTypeStepDao.getStepsWithDetailsByConsultationTypeId(Constants.GENERAL_CONSULTATION_TYPE)


        localConsultationTypeSteps.map { it.toModel() }
    }
}