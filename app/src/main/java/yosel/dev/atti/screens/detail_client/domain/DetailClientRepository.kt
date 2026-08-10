package yosel.dev.atti.screens.detail_client.domain

import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.ClientWithPatientsModel

interface DetailClientRepository {

    suspend fun getClientWithPatients(clientId: String, isLocalPatients: Boolean): Result<ClientWithPatientsModel>

    suspend fun updateClient(client: ClientModel): Result<Unit>

    suspend fun updateClientStatus(clientId: String, newStatus: Int): Result<Unit>

    suspend fun updatePatientsStatus(patientIds: List<String>, newStatus: Int): Result<Unit>
}