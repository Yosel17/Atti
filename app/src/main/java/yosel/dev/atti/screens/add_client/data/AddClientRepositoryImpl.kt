package yosel.dev.atti.screens.add_client.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.supabase.ClientsDataSource
import yosel.dev.atti.core.utils.toDto
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.screens.add_client.domain.AddClientRepository
import javax.inject.Inject

class AddClientRepositoryImpl @Inject constructor(
    private val clientsDataSource: ClientsDataSource,
    private val clientDao: ClientDao
): AddClientRepository {

    override suspend fun insertClient(client: ClientModel): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val clientDto = clientsDataSource.insertAndGetClient(client = client.toDto())
                clientDao.upsertClient(client = clientDto.toEntity())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(exception = e)
            }
        }
    }
}