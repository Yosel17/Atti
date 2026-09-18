package yosel.dev.atti.screens.top_level.services.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

interface ServicesRepository {
    fun getAllServices(): Flow<List<ServiceWithDetailsModel>>
    suspend fun syncServices(): Result<Unit>
}