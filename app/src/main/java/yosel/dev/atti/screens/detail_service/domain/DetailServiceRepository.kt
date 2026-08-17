package yosel.dev.atti.screens.detail_service.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

interface DetailServiceRepository {

    fun getServiceWithDetailsByIdFlow(serviceId: String): Flow<ServiceWithDetailsModel?>

    suspend fun changeStatusService(serviceId: String, newStatus: Int): Result<Unit>
}