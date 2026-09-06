package yosel.dev.atti.screens.detail_client.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.ClientWithPatientsWithDetailsModel
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.supabase.ClientsDataSource
import yosel.dev.atti.core.supabase.PatientsDataSource
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.detail_client.domain.DetailClientRepository
import javax.inject.Inject

class DetailClientRepositoryImpl @Inject constructor(
    private val patientsDataSource: PatientsDataSource,
    private val clientsDataSource: ClientsDataSource,
    private val clientDao: ClientDao,
    private val patientDao: PatientDao
) : DetailClientRepository {

    override fun getClientWithPatientsWithCatalogsFlow(clientId: String): Flow<ClientWithPatientsWithDetailsModel?> {
        return clientDao.getClientWithPatientsWithCatalogsFlow(clientId = clientId)
            .map { entity -> entity?.toModel() }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun syncPatientsIfNeeded(
        clientId: String,
        isLocalPatients: Boolean
    ): Result<Unit> = runCatching {
        if (isLocalPatients) return@runCatching

        val clientExists = clientDao.getClientById(clientId) != null
        if (!clientExists) {
            throw NoSuchElementException("Cliente no encontrado localmente con id: $clientId.")
        }

        val remotePatients = patientsDataSource.getPatientsByClientId(clientId = clientId)
        patientDao.upsertPatients(remotePatients.map { it.toEntity() })
    }

    override suspend fun updateClient(client: ClientModel): Result<Unit> = runCatching {
        clientsDataSource.updateClient(client = client.toDtoForUpdate())
        clientDao.upsertClient(client = client.toEntity())
    }

    override suspend fun updateClientStatus(
        clientId: String,
        newStatus: Int
    ): Result<Unit> = runCatching {
        clientsDataSource.updateClientStatus(clientId = clientId, newStatus = newStatus)
        clientDao.updateClientStatus(clientId = clientId, newStatus = newStatus)
    }

    override suspend fun updatePatientsStatus(
        patientIds: List<String>,
        newStatus: Int
    ): Result<Unit> = runCatching {
        patientsDataSource.updatePatientsStatus(patientIds = patientIds, newStatus = newStatus)
        patientDao.updatePatientsStatus(patientIds = patientIds, newStatus = newStatus)
    }
}