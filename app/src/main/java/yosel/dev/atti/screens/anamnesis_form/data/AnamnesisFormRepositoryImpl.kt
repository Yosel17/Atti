package yosel.dev.atti.screens.anamnesis_form.data

import yosel.dev.atti.core.models.model.AnamnesisDewormingModel
import yosel.dev.atti.core.models.model.AnamnesisEnvironmentOptionModel
import yosel.dev.atti.core.models.model.AnamnesisModel
import yosel.dev.atti.core.models.model.AnamnesisVaccineModel
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.request.CreateAnamnesisRequest
import yosel.dev.atti.core.room.tables.anamnesis.AnamnesisDao
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogDao
import yosel.dev.atti.core.supabase.AnamnesisDataSource
import yosel.dev.atti.core.supabase.AppCatalogsDataSource
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.anamnesis_form.domain.AnamnesisFormRepository
import javax.inject.Inject

class AnamnesisFormRepositoryImpl @Inject constructor(
    private val appCatalogsDataSource: AppCatalogsDataSource,
    private val appCatalogDao: AppCatalogDao,
    private val anamnesisDataSource: AnamnesisDataSource,
    private val anamnesisDao: AnamnesisDao
): AnamnesisFormRepository {

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
    ): Result<Unit> = runCatching {
        // 1. Armar el request para la función RPC
        val request = CreateAnamnesisRequest(
            anamnesisData = anamnesis.toDtoForInsert(),
            environmentOptionsData = environmentOptions.map { it.toDtoForInsert() },
            vaccinesData = vaccines.map { it.toDtoForInsert() },
            dewormingsData = dewormings.map { it.toDtoForInsert() }
        )

        // 2. Ejecución atómica en Supabase (si falla, lanza excepción y revierte en la BD remota)
        val insertedAnamnesisDto = anamnesisDataSource.insertAnamnesisWithDetails(request = request)

        // 3. Ejecución atómica en Room con los IDs generados por Supabase
        anamnesisDao.saveAnamnesisWithDetails(
            anamnesis = insertedAnamnesisDto.toEntity(),
            options = insertedAnamnesisDto.environmentOptions.map { it.toEntity() },
            vaccines = insertedAnamnesisDto.vaccines.map { it.toEntity() },
            dewormings = insertedAnamnesisDto.dewormings.map { it.toEntity() }
        )
    }
}