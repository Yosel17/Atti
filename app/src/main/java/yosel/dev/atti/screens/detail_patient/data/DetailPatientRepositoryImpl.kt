package yosel.dev.atti.screens.detail_patient.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.supabase.ClientsDataSource
import yosel.dev.atti.core.supabase.PatientsDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.detail_patient.domain.DetailPatientRepository
import javax.inject.Inject

class DetailPatientRepositoryImpl @Inject constructor(
    private val patientDao: PatientDao,
    private val clientDao: ClientDao,
    private val clientsDataSource: ClientsDataSource,
    private val patientsDataSource: PatientsDataSource
): DetailPatientRepository {

    override fun getPatientByIdFlow(patientId: String): Flow<Result<PatientModel>> =
        patientDao.getPatientByIdFlow(patientId = patientId)
            .map { entity ->
                if (entity != null) {
                    Result.success(entity.toModel())
                } else {
                    Result.failure(NoSuchElementException("No se encontró el paciente con ID: $patientId"))
                }
            }
            .flowOn(Dispatchers.IO)


    override suspend fun getClientByIdRoom(clientId: String): Result<ClientModel> = runCatching {
        clientDao.getClientById(clientId = clientId)?.toModel()
            ?: throw NoSuchElementException("No se encontró el cliente con ID: $clientId")
    }

    override suspend fun getClientByIdSupabase(clientId: String): Result<ClientModel> = runCatching {
        val clientDto = clientsDataSource.getClientById(id = clientId)
            ?: throw NoSuchElementException("No se encontró el cliente con ID: $clientId")

        clientDao.upsertClient(clientDto.toEntity())
        clientDto.toModel()
    }

    override suspend fun changeStatusPatient(patientId: String, newStatus: Int): Result<Unit> = runCatching{
        patientsDataSource.updatePatientStatus(patientId = patientId, newStatus = newStatus)
        patientDao.updatePatientStatus(patientId = patientId, newStatus = newStatus)
    }
}