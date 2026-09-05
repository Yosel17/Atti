package yosel.dev.atti.screens.detail_client.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.ClientWithPatientsWithDetailsModel

interface DetailClientRepository {
    fun getClientWithPatientsWithCatalogsFlow(clientId: String): Flow<ClientWithPatientsWithDetailsModel?>
    suspend fun syncPatientsIfNeeded(clientId: String, isLocalPatients: Boolean): Result<Unit>
    suspend fun updateClient(client: ClientModel): Result<Unit>
    suspend fun updateClientStatus(clientId: String, newStatus: Int): Result<Unit>
    suspend fun updatePatientsStatus(patientIds: List<String>, newStatus: Int): Result<Unit>
}