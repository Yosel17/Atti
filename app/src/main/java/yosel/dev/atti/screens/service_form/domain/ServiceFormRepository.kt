package yosel.dev.atti.screens.service_form.domain

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceModel
import yosel.dev.atti.core.models.model.ServiceSupplyModel

interface ServiceFormRepository {

    suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>>
    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
    suspend fun insertService(service: ServiceModel): Result<ServiceModel>
    suspend fun insertServiceSupplies(supplies: List<ServiceSupplyModel>): Result<Unit>
    suspend fun getActiveProductsWithDetails(): Result<List<ProductWithDetailsModel>>
}