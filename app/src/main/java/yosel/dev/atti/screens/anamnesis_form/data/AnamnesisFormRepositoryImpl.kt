package yosel.dev.atti.screens.anamnesis_form.data

import androidx.room.withTransaction
import yosel.dev.atti.core.models.model.AnamnesisDewormingModel
import yosel.dev.atti.core.models.model.AnamnesisEnvironmentOptionModel
import yosel.dev.atti.core.models.model.AnamnesisModel
import yosel.dev.atti.core.models.model.AnamnesisVaccineModel
import yosel.dev.atti.core.models.model.AnamnesisWithDetailsModel
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.request.CreateAnamnesisRequest
import yosel.dev.atti.core.room.config.AppDatabase
import yosel.dev.atti.core.room.tables.anamnesis.AnamnesisDao
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.room.tables.consultation.ConsultationDao
import yosel.dev.atti.core.supabase.AnamnesisDataSource
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.anamnesis_form.domain.AnamnesisFormRepository
import javax.inject.Inject

class AnamnesisFormRepositoryImpl @Inject constructor(
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val anamnesisDataSource: AnamnesisDataSource,
    private val anamnesisDao: AnamnesisDao,
    private val consultationDao: ConsultationDao,
    private val appDatabase: AppDatabase
) : AnamnesisFormRepository {

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

    override suspend fun saveAnamnesis(
        anamnesis: AnamnesisModel,
        environmentOptions: List<AnamnesisEnvironmentOptionModel>,
        vaccines: List<AnamnesisVaccineModel>,
        dewormings: List<AnamnesisDewormingModel>
    ): Result<AnamnesisModel> = runCatching {
        // 1. Armar el request para la función RPC
        val request = CreateAnamnesisRequest(
            anamnesisData = anamnesis.toDtoForInsert(),
            environmentOptionsData = environmentOptions.map { it.toDtoForInsert() },
            vaccinesData = vaccines.map { it.toDtoForInsert() },
            dewormingsData = dewormings.map { it.toDtoForInsert() }
        )
        // 2. Ejecución atómica en Supabase
        val insertedAnamnesisDto = anamnesisDataSource.insertAnamnesisWithDetails(request = request)
        // 3. Ejecución atómica en Room con los ID generados por Supabase
        anamnesisDao.saveAnamnesisWithDetails(
            anamnesis = insertedAnamnesisDto.toEntity(),
            options = insertedAnamnesisDto.environmentOptions.map { it.toEntity() },
            vaccines = insertedAnamnesisDto.vaccines.map { it.toEntity() },
            dewormings = insertedAnamnesisDto.dewormings.map { it.toEntity() }
        )
        insertedAnamnesisDto.toModel()
    }

    override suspend fun updateAnamnesisWithDetails(
        anamnesis: AnamnesisModel,
        environmentOptions: List<AnamnesisEnvironmentOptionModel>,
        vaccines: List<AnamnesisVaccineModel>,
        dewormings: List<AnamnesisDewormingModel>
    ): Result<Unit> = runCatching {
        // 1. En Supabase: actualizar anamnesis, eliminar registros hijos anteriores e insertar los nuevos
        anamnesisDataSource.updateAnamnesis(anamnesis.toDtoForUpdate())
        anamnesisDataSource.deleteEnvironmentOptionsByAnamnesisId(anamnesis.id)
        anamnesisDataSource.deleteVaccinesByAnamnesisId(anamnesis.id)
        anamnesisDataSource.deleteDewormingsByAnamnesisId(anamnesis.id)

        val insertedOptionsDto = if (environmentOptions.isNotEmpty()) {
            anamnesisDataSource.insertEnvironmentOptions(environmentOptions.map { it.toDtoForInsert() })
        } else emptyList()

        val insertedVaccinesDto = if (vaccines.isNotEmpty()) {
            anamnesisDataSource.insertVaccines(vaccines.map { it.toDtoForInsert() })
        } else emptyList()

        val insertedDewormingsDto = if (dewormings.isNotEmpty()) {
            anamnesisDataSource.insertDewormings(dewormings.map { it.toDtoForInsert() })
        } else emptyList()

        // 2. En Room: transacción atómica local
        appDatabase.withTransaction {
            anamnesisDao.upsertAnamnesis(anamnesis.toEntity())
            anamnesisDao.deleteEnvironmentOptionsByAnamnesisId(anamnesis.id)
            anamnesisDao.deleteVaccinesByAnamnesisId(anamnesis.id)
            anamnesisDao.deleteDewormingsByAnamnesisId(anamnesis.id)

            if (insertedOptionsDto.isNotEmpty()) {
                anamnesisDao.upsertEnvironmentOptions(insertedOptionsDto.map { it.toEntity() })
            }
            if (insertedVaccinesDto.isNotEmpty()) {
                anamnesisDao.upsertVaccines(insertedVaccinesDto.map { it.toEntity() })
            }
            if (insertedDewormingsDto.isNotEmpty()) {
                anamnesisDao.upsertDewormings(insertedDewormingsDto.map { it.toEntity() })
            }
        }
    }

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(
            consultationId = consultationId
        ) ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getAnamnesisWithDetailsByIdRoom(anamnesisId: String): Result<AnamnesisWithDetailsModel> = runCatching {
        anamnesisDao.getAnamnesisWithDetailsById(anamnesisId)?.toModel()
            ?: throw NoSuchElementException("No se encontró la anamnesis con ID: $anamnesisId")
    }
}