package yosel.dev.atti.screens.top_level.patients.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.PatientWithDetailsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.supabase.PatientsDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.top_level.patients.domain.PatientsRepository
import javax.inject.Inject

class PatientsRepositoryImpl @Inject constructor(
    private val patientDao: PatientDao,
    private val appCatalogDao: AppCatalogDao,
    private val patientsDataSource: PatientsDataSource
) : PatientsRepository {
    override fun getAllPatientsWithCatalogs(): Flow<List<PatientWithDetailsModel>> =
        patientDao.getAllPatientsWithCatalogsFlow()
            .map { entities -> entities.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    override suspend fun syncPatients(): Result<Unit> = runCatching {
        val remotePatients = patientsDataSource.getAllPatientsWithCatalogs()
        val appCatalogsEntities = remotePatients.flatMap { patient ->
            listOfNotNull(
                patient.species?.toEntity(),
                patient.gender?.toEntity()
            )
        }.distinctBy { it.id }
        appCatalogDao.insertAllCatalogs(appCatalogsEntities)
        patientDao.upsertPatients(remotePatients.map { it.toEntity() })
    }
}