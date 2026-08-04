package yosel.dev.atti.screens.clients.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ClientModel

interface ClientsRepository {

    fun getAllBills(): Flow<List<ClientModel>>

    suspend fun syncBills(): Result<Unit>
}