package yosel.dev.atti.screens.detail_service.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.room.tables.service.ServiceDao
import yosel.dev.atti.core.supabase.ServicesDataSource
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.detail_service.domain.DetailServiceRepository
import javax.inject.Inject

class DetailServiceRepositoryImpl @Inject constructor(
    private val serviceDao: ServiceDao,
    private val servicesDataSource: ServicesDataSource
): DetailServiceRepository {

    override fun getServiceWithDetailsByIdFlow(serviceId: String): Flow<ServiceWithDetailsModel?> =
        serviceDao.getServiceWithCatalogByIdFlow(serviceId = serviceId)
            .map { entity -> entity?.toModel() }
            .flowOn(Dispatchers.IO)


    override suspend fun changeStatusService(
        serviceId: String,
        newStatus: Int
    ): Result<Unit> = runCatching {
        servicesDataSource.updateServiceStatus(serviceId = serviceId, newStatus = newStatus)
        serviceDao.updateServiceStatus(serviceId = serviceId, newStatus = newStatus)
    }
}