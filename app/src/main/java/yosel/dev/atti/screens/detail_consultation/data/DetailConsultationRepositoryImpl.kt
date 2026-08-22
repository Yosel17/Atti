package yosel.dev.atti.screens.detail_consultation.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.detail_consultation.domain.DetailConsultationRepository
import javax.inject.Inject
import kotlin.collections.map

class DetailConsultationRepositoryImpl @Inject constructor(
    private val consultationDao: ConsultationDao,
    private val appCatalogDao: AppCatalogDao,
    private val appCatalogsDataSource: AppCatalogsDataSource
): DetailConsultationRepository {

    override fun getConsultationWithDetailsFlow(consultationId: String): Flow<ConsultationWithDetailsModel?> {
        return consultationDao.getConsultationWithDetailsByIdFlow(consultationId = consultationId)
            .map { entity -> entity?.toModel() }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getConsultationSteps(): Result<List<AppCatalogModel>> = runCatching {
        val remoteCatalogs = appCatalogsDataSource.getCatalogsByTypes(listOf(Constants.CONSULTATION_STEPS_TYPE_CATALOG))
        val entities = remoteCatalogs.map { it.toEntity() }
        appCatalogDao.insertAllCatalogs(entities)
        val localCatalogs = appCatalogDao.getCatalogsByTypeId(Constants.CONSULTATION_STEPS_TYPE_CATALOG)
        localCatalogs.map { it.toModel() }
    }
}