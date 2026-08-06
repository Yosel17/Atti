package yosel.dev.atti.screens.detail_client.domain

import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.ClientWithPatientsModel
import yosel.dev.atti.core.models.model.PatientModel

interface DetailClientRepository {

    suspend fun getInfoClient(clientId: String): Result<ClientModel>

    suspend fun getPatientsForClient(clientId: String): Result<List<PatientModel>>

    suspend fun getClientWithPatients(clientId: String): Result<ClientWithPatientsModel>
}