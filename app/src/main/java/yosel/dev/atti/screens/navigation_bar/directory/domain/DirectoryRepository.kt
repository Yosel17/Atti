package yosel.dev.atti.screens.navigation_bar.directory.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel

interface DirectoryRepository {

    fun getAllClients(): Flow<List<ClientModel>>

    suspend fun syncClients(): Result<Unit>

    fun getAllPatientsWithCatalogs(): Flow<List<PatientWithCatalogsModel>>

    suspend fun syncPatients(): Result<Unit>
}