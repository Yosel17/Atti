package yosel.dev.atti.screens.service_form.domain

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceModel
import yosel.dev.atti.core.models.model.ServiceSupplyModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

interface ServiceFormRepository {
    suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>>
    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>
    suspend fun insertServiceWithSupplies(service: ServiceModel, supplies: List<ServiceSupplyModel>): Result<ServiceModel>
    suspend fun updateServiceWithSupplies(service: ServiceModel, supplies: List<ServiceSupplyModel>): Result<Unit>
    suspend fun getServiceByIdRoom(serviceId: String): Result<ServiceWithDetailsModel>
    suspend fun getActiveProductsWithDetails(): Result<List<ProductWithDetailsModel>>
}