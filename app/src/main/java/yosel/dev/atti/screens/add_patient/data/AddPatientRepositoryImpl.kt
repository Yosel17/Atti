package yosel.dev.atti.screens.add_patient.data

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.ClientsDataSource
import yosel.dev.atti.core.supabase.PatientsDataSource
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toDtoInsert
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.add_patient.domain.AddPatientRepository
import javax.inject.Inject

class AddPatientRepositoryImpl @Inject constructor(
    private val patientsDataSource: PatientsDataSource,
    private val clientsDataSource: ClientsDataSource,
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val patientDao: PatientDao,
    private val clientDao: ClientDao
): AddPatientRepository {

    override suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>> = runCatching {
        val remoteAppCatalogs = appCatalogsDataSource.getCatalogsByTypes(types = types)
        val entities = remoteAppCatalogs.map { it.toEntity() }
        appCatalogDao.insertAllCatalogs(catalogs = entities)
        remoteAppCatalogs.map { it.toModel() }
    }

    override suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel> = runCatching {
        val appCatalogDto = appCatalogsDataSource.insertAndGetCatalog(catalog = catalog.toDtoForInsert())
        appCatalogDao.insertCatalog(catalog = appCatalogDto.toEntity())
        appCatalogDto.toModel()
    }

    override suspend fun insertPatient(patient: PatientModel): Result<Unit> = runCatching {
        val patientDto = patientsDataSource.insertAndGetPatient(patient = patient.toDtoInsert())
        patientDao.upsertPatient(patient = patientDto.toEntity())
    }

    override suspend fun updatePatient(patient: PatientModel): Result<Unit> = runCatching {
        patientsDataSource.updatePatient(patient = patient.toDtoForUpdate())
        patientDao.upsertPatient(patient = patient.toEntity())
    }

    override suspend fun getPatientByIdRoom(patientId: String): Result<PatientModel> = runCatching {
        patientDao.getPatientById(patientId = patientId)?.toModel()
            ?: throw NoSuchElementException("No se encontró el paciente con ID: $patientId")
    }

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

    override suspend fun getClients(): Result<List<ClientModel>> = runCatching {
        val clientsEntities = clientDao.getAllClients()
        clientsEntities.map { it.toModel() }
    }
}