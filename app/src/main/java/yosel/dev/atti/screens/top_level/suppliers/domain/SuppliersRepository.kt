package yosel.dev.atti.screens.top_level.suppliers.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.SupplierModel

interface SuppliersRepository {
    fun getAllSuppliers(): Flow<List<SupplierModel>>
    suspend fun syncSuppliers(): Result<Unit>
}