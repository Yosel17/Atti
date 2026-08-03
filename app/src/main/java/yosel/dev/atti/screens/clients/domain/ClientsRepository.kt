package yosel.dev.atti.screens.clients.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ClientsModel

interface ClientsRepository {

    fun getAllBills(): Flow<List<ClientsModel>>

    suspend fun syncBills(): Result<Unit>
}