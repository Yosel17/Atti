package yosel.dev.atti.screens.navigation_bar.inventory.data

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.screens.navigation_bar.inventory.domain.InventoryRepository
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(

): InventoryRepository {

    override fun getAllProducts(): Flow<List<ProductWithDetailsModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun syncProducts(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getAllServices(): Flow<List<ServiceWithDetailsModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun syncServices(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getAllSuppliers(): Flow<List<SupplierModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun syncSuppliers(): Result<Unit> {
        TODO("Not yet implemented")
    }
}