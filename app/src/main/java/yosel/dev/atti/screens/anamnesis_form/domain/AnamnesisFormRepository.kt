package yosel.dev.atti.screens.anamnesis_form.domain

import yosel.dev.atti.core.models.model.AppCatalogModel

interface AnamnesisFormRepository {

    suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>>

    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
}