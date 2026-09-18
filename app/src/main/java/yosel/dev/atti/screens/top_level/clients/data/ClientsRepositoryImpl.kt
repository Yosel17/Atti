package yosel.dev.atti.screens.top_level.clients.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.supabase.ClientsDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.top_level.clients.domain.ClientsRepository
import javax.inject.Inject

class ClientsRepositoryImpl @Inject constructor(
    private val clientDao: ClientDao,
    private val clientsDataSource: ClientsDataSource
) : ClientsRepository {
    override fun getAllClients(): Flow<List<ClientModel>> =
        clientDao.getAllClientsFlow()
            .map { entities -> entities.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    override suspend fun syncClients(): Result<Unit> = runCatching {
        val remoteClients = clientsDataSource.getAllClients()
        clientDao.upsertClients(remoteClients.map { it.toEntity() })
    }
}