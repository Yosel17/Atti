package yosel.dev.atti.screens.detail_client.data

import yosel.dev.atti.core.models.model.ClientWithPatientsModel
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

    override suspend fun getClientWithPatients(
        clientId: String,
        isLocal: Boolean
    ): Result<ClientWithPatientsModel> = runCatching {
        if (isLocal) {
            return@runCatching fetchLocalClientWithPatients(clientId)
        }

        val clientExists = clientDao.getClientById(clientId) != null
        if (!clientExists) {
            throw NoSuchElementException("Cliente no encontrado localmente con id: $clientId. No se puede sincronizar pacientes sin el cliente.")
        }

        val remotePatients = patientsDataSource.getPatientsByClientId(clientId = clientId)
        patientDao.upsertPatients(remotePatients.map { it.toEntity() })

        fetchLocalClientWithPatients(clientId)
    }

    private suspend fun fetchLocalClientWithPatients(clientId: String): ClientWithPatientsModel {
        return clientDao.getClientWithPatients(clientId = clientId)?.toModel()
            ?: throw NoSuchElementException("Cliente no encontrado con id: $clientId")
    }
}