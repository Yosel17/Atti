package yosel.dev.atti.screens.clients.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.supabase.ClientsDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.clients.domain.ClientsRepository
import javax.inject.Inject

class ClientsRepositoryImpl @Inject constructor(
    private val clientsDataSource: ClientsDataSource,
    private val clientDao: ClientDao
): ClientsRepository {

    override fun getAllBills(): Flow<List<ClientModel>> {
        return clientDao.getAllClients()
            .map { entities ->
                entities.map { it.toModel() }
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncBills(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val remoteClients = clientsDataSource.getAllClients()
                val entities = remoteClients.map { it.toEntity() }

                clientDao.upsertClients(entities)

                Result.success(Unit)
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                Result.failure(e)
            }
        }
    }
}