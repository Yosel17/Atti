package yosel.dev.atti.screens.top_level.consultations.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.client.ClientDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.room.tables.patient.PatientDao
import yosel.dev.atti.core.supabase.ConsultationsDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.top_level.consultations.domain.ConsultationsRepository
import javax.inject.Inject

class ConsultationsRepositoryImpl @Inject constructor(
    private val consultationDao: ConsultationDao,
    private val consultationsDataSource: ConsultationsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val clientDao: ClientDao,
    private val patientDao: PatientDao
) : ConsultationsRepository {

    override fun getAllConsultationsWithDetails(): Flow<List<ConsultationWithDetailsModel>> =
        consultationDao.getAllConsultationsWithDetailsFlow()
            .map { entities -> entities.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    override suspend fun syncConsultations(): Result<Unit> = runCatching {
        val remoteConsultations = consultationsDataSource.getAllConsultationsWithDetails()

        // 1. Catálogos relacionados (tipo de consulta, especie y género)
        val catalogEntities = remoteConsultations.flatMap { dto ->
            listOfNotNull(
                dto.consultationType?.toEntity(),
                dto.patient?.species?.toEntity(),
                dto.patient?.gender?.toEntity()
            )
        }.distinctBy { it.id }

        // 2. Propietarios / Clientes
        val clientEntities = remoteConsultations.mapNotNull {
            it.patient?.client?.toEntity()
        }.distinctBy { it.id }

        // 3. Pacientes
        val patientEntities = remoteConsultations.mapNotNull {
            it.patient?.toEntity()
        }.distinctBy { it.id }

        // 4. Consultas
        val consultationEntities = remoteConsultations.map {
            it.toEntity()
        }.distinctBy { it.id }

        // Inserciones en Room manteniendo integridad referencial
        if (catalogEntities.isNotEmpty()) appCatalogDao.insertAllCatalogs(catalogEntities)
        if (clientEntities.isNotEmpty()) clientDao.upsertClients(clientEntities)
        if (patientEntities.isNotEmpty()) patientDao.upsertPatients(patientEntities)
        if (consultationEntities.isNotEmpty()) consultationDao.upsertConsultations(consultationEntities)
    }
}