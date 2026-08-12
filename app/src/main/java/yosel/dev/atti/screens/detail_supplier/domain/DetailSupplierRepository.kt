package yosel.dev.atti.screens.detail_supplier.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.SupplierModel

interface DetailSupplierRepository {

    suspend fun getSupplierByIdFlow(supplierId: String): Flow<SupplierModel>

    suspend fun updateSupplier(supplier: SupplierModel): Result<Unit>

    suspend fun updateSupplierStatus(supplierId: String, newStatus: Int): Result<Unit>
}