package yosel.dev.atti.screens.navigation_bar.consultation.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.dto.ConsultationDto
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PatientWithDetailsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.supabase.ClientsDataSource
import yosel.dev.atti.core.supabase.ConsultationsDataSource
import yosel.dev.atti.core.supabase.PatientsDataSource
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.navigation_bar.consultation.domain.ConsultationRepository
import javax.inject.Inject

class ConsultationRepositoryImpl @Inject constructor(
    private val consultationDao: ConsultationDao,
    private val patientDao: PatientDao,
    private val appCatalogDao: AppCatalogDao,
    private val consultationsDataSource: ConsultationsDataSource,
    private val patientsDataSource: PatientsDataSource,
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val clientsDataSource: ClientsDataSource,
    private val clientDao: ClientDao
) : ConsultationRepository {

    override fun getActiveConsultationFlow(): Flow<ConsultationWithDetailsModel?> =
        consultationDao.getActiveConsultationWithDetailsFlow()
            .map { entity -> entity?.toModel() }
            .flowOn(Dispatchers.IO)

    override suspend fun syncActiveConsultation(): Result<Unit> = runCatching {
        val remoteConsultations = consultationsDataSource.getConsultationsWithDetailsByStatus(Constants.ACTIVE_STATUS)
        val allCatalogEntities = remoteConsultations.flatMap { consultation ->
            listOfNotNull(
                consultation.consultationType?.toEntity(),
                consultation.patient?.species?.toEntity(),
                consultation.patient?.gender?.toEntity()
            )
        }.distinctBy { it.id }
        val clientEntities = remoteConsultations.mapNotNull { it.patient?.client?.toEntity() }
        val patientEntities = remoteConsultations.mapNotNull { it.patient?.toEntity() }
        val consultationEntities = remoteConsultations.map { it.toEntity() }

        if (allCatalogEntities.isNotEmpty()) {
            appCatalogDao.insertAllCatalogs(allCatalogEntities)
        }
        if (clientEntities.isNotEmpty()){
            clientDao.upsertClients(clientEntities)
        }
        if (patientEntities.isNotEmpty()) {
            patientDao.upsertPatients(patientEntities)
        }
        if (consultationEntities.isNotEmpty()) {
            consultationDao.upsertConsultations(consultationEntities)
        }
    }

    override fun getAllPatientsWithCatalogsFlow(): Flow<List<PatientWithDetailsModel>> =
        patientDao.getAllPatientsWithCatalogsFlow()
            .map { entities ->
                entities.map { it.toModel() }
            }
            .flowOn(Dispatchers.IO)

    override suspend fun syncClientsAndPatients(): Result<Unit> = runCatching {
        //clients
        val remoteClients = clientsDataSource.getAllClients()
        val clientsEntities = remoteClients.map { it.toEntity() }

        clientDao.upsertClients(clientsEntities)

        //patients
        val remotePatients = patientsDataSource.getAllPatientsWithCatalogs()
        val patientsEntities = remotePatients.map { it.toEntity() }
        val appCatalogsEntities = remotePatients.flatMap { patient ->
            listOfNotNull(
                patient.species?.toEntity(),
                patient.gender?.toEntity()
            )
        }.distinctBy { it.id }
        appCatalogDao.insertAllCatalogs(appCatalogsEntities)
        patientDao.upsertPatients(patientsEntities)
    }

    override suspend fun getConsultationReasons(): Result<List<AppCatalogModel>> = runCatching {
        val remoteCatalogs = appCatalogsDataSource.getCatalogsByTypes(listOf(Constants.CONSULTATION_REASON_TYPE_CATALOG))
        val entities = remoteCatalogs.map { it.toEntity() }
        appCatalogDao.insertAllCatalogs(entities)
        val localCatalogs = appCatalogDao.getCatalogsByTypeId(Constants.CONSULTATION_REASON_TYPE_CATALOG)
        localCatalogs.map { it.toModel() }
    }

    override suspend fun createConsultation(
        patientId: String,
        consultationTypeId: Int
    ): Result<ConsultationModel> = runCatching {
        val newConsultationDto = ConsultationDto(
            patientId = patientId,
            consultationTypeId = consultationTypeId,
            status = Constants.ACTIVE_STATUS
        )
        val insertedDto = consultationsDataSource.insertAndGetConsultation(newConsultationDto)
        consultationDao.upsertConsultation(insertedDto.toEntity())
        insertedDto.toModel()
    }
}