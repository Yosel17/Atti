package yosel.dev.atti.screens.anamnesis_form.domain

import yosel.dev.atti.core.models.model.AnamnesisDewormingModel
import yosel.dev.atti.core.models.model.AnamnesisEnvironmentOptionModel
import yosel.dev.atti.core.models.model.AnamnesisModel
import yosel.dev.atti.core.models.model.AnamnesisVaccineModel
import yosel.dev.atti.core.models.model.AnamnesisWithDetailsModel
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

interface AnamnesisFormRepository {
    suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>>
    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
    suspend fun saveAnamnesis(
        anamnesis: AnamnesisModel,
        environmentOptions: List<AnamnesisEnvironmentOptionModel>,
        vaccines: List<AnamnesisVaccineModel>,
        dewormings: List<AnamnesisDewormingModel>
    ): Result<AnamnesisModel>
    suspend fun updateAnamnesisWithDetails(
        anamnesis: AnamnesisModel,
        environmentOptions: List<AnamnesisEnvironmentOptionModel>,
        vaccines: List<AnamnesisVaccineModel>,
        dewormings: List<AnamnesisDewormingModel>
    ): Result<Unit>
    suspend fun getConsultation(consultationId: String): Result<ConsultationWithDetailsModel>
    suspend fun getAnamnesisWithDetailsByIdRoom(anamnesisId: String): Result<AnamnesisWithDetailsModel>
}