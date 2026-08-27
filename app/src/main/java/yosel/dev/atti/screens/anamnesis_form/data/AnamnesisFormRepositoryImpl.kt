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
import yosel.dev.atti.core.models.request.UpdateAnamnesisRequest
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
        val request = CreateAnamnesisRequest(
            anamnesisData = anamnesis.toDtoForInsert(),
            environmentOptionsData = environmentOptions.map { it.toDtoForInsert() },
            vaccinesData = vaccines.map { it.toDtoForInsert() },
            dewormingsData = dewormings.map { it.toDtoForInsert() }
        )
        val insertedAnamnesisDto = anamnesisDataSource.insertAnamnesisWithDetails(request = request)
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
        environmentOptions: List<AnamnesisEnvironmentOptionModel>?,
        vaccines: List<AnamnesisVaccineModel>?,
        dewormings: List<AnamnesisDewormingModel>?
    ): Result<Unit> = runCatching {
        // 1. Supabase: Ejecutar la función RPC atómica
        val request = UpdateAnamnesisRequest(
            anamnesisData = anamnesis.toDtoForUpdate(),
            environmentOptionsData = environmentOptions?.map { it.toDtoForInsert() },
            vaccinesData = vaccines?.map { it.toDtoForInsert() },
            dewormingsData = dewormings?.map { it.toDtoForInsert() }
        )
        anamnesisDataSource.updateAnamnesisWithDetails(request)

        // 2. Room: Ejecución atómica local
        appDatabase.withTransaction {
            anamnesisDao.upsertAnamnesis(anamnesis.toEntity())

            if (environmentOptions != null) {
                anamnesisDao.deleteEnvironmentOptionsByAnamnesisId(anamnesis.id)
                if (environmentOptions.isNotEmpty()) {
                    anamnesisDao.upsertEnvironmentOptions(environmentOptions.map { it.toEntity() })
                }
            }

            if (vaccines != null) {
                anamnesisDao.deleteVaccinesByAnamnesisId(anamnesis.id)
                if (vaccines.isNotEmpty()) {
                    anamnesisDao.upsertVaccines(vaccines.map { it.toEntity() })
                }
            }

            if (dewormings != null) {
                anamnesisDao.deleteDewormingsByAnamnesisId(anamnesis.id)
                if (dewormings.isNotEmpty()) {
                    anamnesisDao.upsertDewormings(dewormings.map { it.toEntity() })
                }
            }
        }
    }

    override suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel> = runCatching {
        val consultationEntity = consultationDao.getConsultationWithDetailsById(
            consultationId = consultationId
        ) ?: throw IllegalStateException("No se pudo recuperar la información de la consulta")
        consultationEntity.toModel()
    }

    override suspend fun getAnamnesisWithDetailsById(anamnesisId: String): Result<AnamnesisWithDetailsModel> = runCatching {
        // 1. Intentar obtener de Room
        val localAnamnesis = anamnesisDao.getAnamnesisWithDetailsById(anamnesisId)
        if (localAnamnesis != null) {
            return@runCatching localAnamnesis.toModel()
        }

        // 2. Si no existe en Room, buscar en Supabase
        val remoteDto = anamnesisDataSource.getAnamnesisWithDetailsById(anamnesisId)
            ?: throw NoSuchElementException("No se encontró la anamnesis con ID: $anamnesisId")

        // 3. Sincronizar catálogos embebidos en Room para mantener consistencia de relaciones
        val catalogsToInsert = buildList {
            remoteDto.foodBrand?.let { add(it.toEntity()) }
            remoteDto.foodUnit?.let { add(it.toEntity()) }
            remoteDto.environmentOptions.forEach { opt ->
                opt.catalog?.let { add(it.toEntity()) }
            }
            remoteDto.vaccines.forEach { vac ->
                vac.vaccine?.let { add(it.toEntity()) }
                vac.scheme?.let { add(it.toEntity()) }
            }
            remoteDto.dewormings.forEach { dew ->
                dew.product?.let { add(it.toEntity()) }
            }
        }.distinctBy { it.id }

        if (catalogsToInsert.isNotEmpty()) {
            appCatalogDao.insertAllCatalogs(catalogsToInsert)
        }

        // 4. Guardar datos en Room
        anamnesisDao.saveAnamnesisWithDetails(
            anamnesis = remoteDto.toEntity(),
            options = remoteDto.environmentOptions.map { it.toEntity() },
            vaccines = remoteDto.vaccines.map { it.toEntity() },
            dewormings = remoteDto.dewormings.map { it.toEntity() }
        )

        // 5. Retornar los datos desde Room
        anamnesisDao.getAnamnesisWithDetailsById(anamnesisId)?.toModel()
            ?: throw IllegalStateException("Error al recuperar la anamnesis guardada localmente")
    }
}