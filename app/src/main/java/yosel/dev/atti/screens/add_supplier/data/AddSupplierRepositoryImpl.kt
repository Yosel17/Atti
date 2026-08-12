package yosel.dev.atti.screens.add_supplier.data

import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.supabase.SuppliersDataSource
import yosel.dev.atti.core.utils.toDtoForInsert
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.screens.add_supplier.domain.AddSupplierRepository
import javax.inject.Inject

class AddSupplierRepositoryImpl @Inject constructor(
    private val suppliersDataSource: SuppliersDataSource,
    private val supplierDao: SupplierDao
): AddSupplierRepository {

    override suspend fun insertSupplier(supplier: SupplierModel): Result<Unit> = runCatching {
        val supplierDto = suppliersDataSource.insertAndGetSupplier(supplier = supplier.toDtoForInsert())
        supplierDao.upsertSupplier(supplier = supplierDto.toEntity())
    }
}