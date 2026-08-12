package yosel.dev.atti.screens.navigation_bar.directory.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
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
    private val patientDao: PatientDao,
    private val appCatalogDao: AppCatalogDao
): DirectoryRepository {

    override fun getAllClients(): Flow<List<ClientModel>> =
        clientDao.getAllClientsFlow()
            .map { entities ->
                entities.map { it.toModel() }
            }
            .flowOn(Dispatchers.IO)


    override suspend fun syncClients(): Result<Unit> = runCatching {
        val remoteClients = clientsDataSource.getAllClients()
        val entities = remoteClients.map { it.toEntity() }

        clientDao.upsertClients(entities)
    }

    override fun getAllPatientsWithCatalogs(): Flow<List<PatientWithCatalogsModel>> =
        patientDao.getAllPatientsWithCatalogsFlow()
            .map { entities ->
                entities.map { it.toModel() }
            }
            .flowOn(Dispatchers.IO)


    override suspend fun syncPatients(): Result<Unit> = runCatching {
        val remotePatients = patientsDataSource.getAllPatientsWithCatalogs()
        val entities = remotePatients.map { it.toEntity() }
        val appCatalogsEntities = remotePatients.flatMap { patient ->
            listOfNotNull(
                patient.species?.toEntity(),
                patient.gender?.toEntity()
            )
        }.distinctBy { it.id }

        appCatalogDao.insertAllCatalogs(appCatalogsEntities)
        patientDao.upsertPatients(entities)
    }
}