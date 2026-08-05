package yosel.dev.atti.screens.directory.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ClientModel

interface DirectoryRepository {

    fun getAllClients(): Flow<List<ClientModel>>

    suspend fun syncClients(): Result<Unit>
}