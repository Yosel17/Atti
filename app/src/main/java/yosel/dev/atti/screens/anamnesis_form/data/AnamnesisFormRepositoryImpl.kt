package yosel.dev.atti.screens.anamnesis_form.data

import yosel.dev.atti.core.models.model.AnamnesisDewormingModel
import yosel.dev.atti.core.models.model.AnamnesisEnvironmentOptionModel
import yosel.dev.atti.core.models.model.AnamnesisModel
import yosel.dev.atti.core.models.model.AnamnesisVaccineModel
import yosel.dev.atti.core.models.model.AppCatalogModel
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
        // 1. Insertar registro principal en Supabase
        val insertedAnamnesisDto = anamnesisDataSource.insertAndGetAnamnesis(
            anamnesis = anamnesis.toDtoForInsert()
        )
        val generatedAnamnesisId = insertedAnamnesisDto.id
            ?: throw IllegalStateException("No se pudo obtener el ID de la anamnesis generada")

        // 2. Asociar el ID generado a las tablas dependientes y mapear a DTOs
        val envDtos = environmentOptions.map {
            it.copy(anamnesisId = generatedAnamnesisId).toDtoForInsert()
        }
        val vaccineDtos = vaccines.map {
            it.copy(anamnesisId = generatedAnamnesisId).toDtoForInsert()
        }
        val dewormingDtos = dewormings.map {
            it.copy(anamnesisId = generatedAnamnesisId).toDtoForInsert()
        }

        // 3. Insertar registros dependientes en Supabase
        val insertedEnvDtos = anamnesisDataSource.insertEnvironmentOptions(envDtos)
        val insertedVaccineDtos = anamnesisDataSource.insertVaccines(vaccineDtos)
        val insertedDewormingDtos = anamnesisDataSource.insertDewormings(dewormingDtos)

        // 4. Sincronizar en Room
        anamnesisDao.upsertAnamnesis(insertedAnamnesisDto.toEntity())
        if (insertedEnvDtos.isNotEmpty()) {
            anamnesisDao.upsertEnvironmentOptions(insertedEnvDtos.map { it.toEntity() })
        }
        if (insertedVaccineDtos.isNotEmpty()) {
            anamnesisDao.upsertVaccines(insertedVaccineDtos.map { it.toEntity() })
        }
        if (insertedDewormingDtos.isNotEmpty()) {
            anamnesisDao.upsertDewormings(insertedDewormingDtos.map { it.toEntity() })
        }
    }
}