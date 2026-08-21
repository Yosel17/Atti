package yosel.dev.atti.screens.navigation_bar.consultation.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.dto.ConsultationDto
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
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
    private val appCatalogsDataSource: AppCatalogsDataSource
) : ConsultationRepository {

    override fun getActiveConsultationFlow(): Flow<ConsultationWithDetailsModel?> =
        consultationDao.getAllConsultationsWithDetailsFlow()
            .map { list ->
                list.firstOrNull { it.consultation.status == Constants.ACTIVE_STATUS }?.toModel()
            }
            .flowOn(Dispatchers.IO)

    override suspend fun syncActiveConsultation(): Result<Unit> = runCatching {
        val remoteConsultations = consultationsDataSource.getConsultationsWithDetailsByStatus(Constants.ACTIVE_STATUS)
        val consultationEntities = remoteConsultations.map { it.toEntity() }
        val catalogEntities = remoteConsultations.mapNotNull { it.consultationType?.toEntity() }
        val patientEntities = remoteConsultations.mapNotNull { it.patient?.toEntity() }

        if (catalogEntities.isNotEmpty()) {
            appCatalogDao.insertAllCatalogs(catalogEntities)
        }
        if (patientEntities.isNotEmpty()) {
            patientDao.upsertPatients(patientEntities)
        }
        if (consultationEntities.isNotEmpty()) {
            consultationDao.upsertConsultations(consultationEntities)
        }
    }

    override fun getAllPatientsWithCatalogsFlow(): Flow<List<PatientWithCatalogsModel>> =
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