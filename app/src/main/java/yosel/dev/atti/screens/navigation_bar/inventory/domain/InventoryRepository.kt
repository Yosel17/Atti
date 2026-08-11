package yosel.dev.atti.screens.navigation_bar.inventory.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.SupplierModel

interface InventoryRepository {

    fun getAllProducts(): Flow<List<ProductWithDetailsModel>>

    suspend fun syncProducts(): Result<Unit>

    fun getAllServices(): Flow<List<ServiceWithDetailsModel>>

    suspend fun syncServices(): Result<Unit>

    fun getAllSuppliers(): Flow<List<SupplierModel>>

    suspend fun syncSuppliers(): Result<Unit>
}