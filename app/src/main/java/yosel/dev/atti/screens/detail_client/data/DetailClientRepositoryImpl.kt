package yosel.dev.atti.screens.detail_client.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.ClientWithPatientsModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.supabase.PatientsDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.detail_client.domain.DetailClientRepository
import javax.inject.Inject

class DetailClientRepositoryImpl @Inject constructor(
    private val patientsDataSource: PatientsDataSource,
    private val clientDao: ClientDao,
    private val patientDao: PatientDao
) : DetailClientRepository {

    override suspend fun getInfoClient(clientId: String): Result<ClientModel> = runCatching {
        val clientEntity = clientDao.getClientById(clientId = clientId)
            ?: throw NoSuchElementException("Cliente no encontrado con id: $clientId")

        clientEntity.toModel()
    }

    override suspend fun getPatientsForClient(clientId: String): Result<List<PatientModel>> = runCatching {
        val remotePatients = patientsDataSource.getPatientsByClientId(clientId = clientId)
        patientDao.upsertPatients(remotePatients.map { it.toEntity() })

        val localPatients = patientDao.getPatientsByClientId(clientId = clientId)
        localPatients.map { it.toModel() }
    }

    override suspend fun getClientWithPatients(clientId: String): Result<ClientWithPatientsModel> = runCatching {
        val clientWithPatients = clientDao.getClientWithPatients(clientId = clientId)
            ?: throw NoSuchElementException("Cliente no encontrado con id: $clientId")

        clientWithPatients.toModel()
    }
}