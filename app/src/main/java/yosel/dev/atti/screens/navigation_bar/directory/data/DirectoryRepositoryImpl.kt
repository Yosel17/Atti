package yosel.dev.atti.screens.navigation_bar.directory.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.supabase.ClientsDataSource
import yosel.dev.atti.core.supabase.PatientsDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.navigation_bar.directory.domain.DirectoryRepository
import javax.inject.Inject

class DirectoryRepositoryImpl @Inject constructor(
    private val clientsDataSource: ClientsDataSource,
    private val clientDao: ClientDao,
    private val patientsDataSource: PatientsDataSource,
    private val patientDao: PatientDao
): DirectoryRepository {

    override fun getAllClients(): Flow<List<ClientModel>> {
        return clientDao.getAllClients()
            .map { entities ->
                entities.map { it.toModel() }
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncClients(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val remoteClients = clientsDataSource.getAllClients()
                val entities = remoteClients.map { it.toEntity() }

                clientDao.clearAndInsertClients(entities)

                Result.success(Unit)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Result.failure(e)
            }
        }
    }

    override fun getAllPatients(): Flow<List<PatientModel>> {
        return patientDao.getAllPatients()
            .map { entities ->
                entities.map { it.toModel() }
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncPatients(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val remotePatients = patientsDataSource.getAllPatients()
                val entities = remotePatients.map { it.toEntity() }

                patientDao.clearAndInsertPatients(entities)

                Result.success(Unit)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Result.failure(e)
            }
        }
    }
}