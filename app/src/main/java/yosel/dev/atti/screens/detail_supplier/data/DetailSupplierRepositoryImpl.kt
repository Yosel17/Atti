package yosel.dev.atti.screens.detail_supplier.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.supabase.SuppliersDataSource
import yosel.dev.atti.core.utils.toDtoForUpdate
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.detail_supplier.domain.DetailSupplierRepository
import javax.inject.Inject

class DetailSupplierRepositoryImpl @Inject constructor(
    private val supplierDao: SupplierDao,
    private val suppliersDataSource: SuppliersDataSource
): DetailSupplierRepository {

    override suspend fun getSupplierByIdFlow(supplierId: String): Flow<SupplierModel?> =
        supplierDao.getSupplierByIdFlow(supplierId = supplierId)
            .map { entity -> entity?.toModel() }
            .flowOn(Dispatchers.IO)

    override suspend fun updateSupplier(supplier: SupplierModel): Result<Unit> = runCatching {
        suppliersDataSource.updateSupplier(supplier = supplier.toDtoForUpdate())
        supplierDao.upsertSupplier(supplier = supplier.toEntity())
    }

    override suspend fun updateSupplierStatus(
        supplierId: String,
        newStatus: Int
    ): Result<Unit> = runCatching {
        suppliersDataSource.updateSupplierStatus(supplierId = supplierId, newStatus = newStatus)
        supplierDao.updateSupplierStatus(supplierId = supplierId, newStatus = newStatus)
    }
}