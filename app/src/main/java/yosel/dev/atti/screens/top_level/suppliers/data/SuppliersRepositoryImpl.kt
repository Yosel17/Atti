package yosel.dev.atti.screens.top_level.suppliers.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.room.tables.supplier.SupplierDao
import yosel.dev.atti.core.supabase.SuppliersDataSource
import yosel.dev.atti.core.utils.toEntity
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.top_level.suppliers.domain.SuppliersRepository
import javax.inject.Inject

class SuppliersRepositoryImpl @Inject constructor(
    private val supplierDao: SupplierDao,
    private val suppliersDataSource: SuppliersDataSource
) : SuppliersRepository {

    override fun getAllSuppliers(): Flow<List<SupplierModel>> =
        supplierDao.getAllSuppliersFlow()
            .map { entities -> entities.map { it.toModel() } }
            .flowOn(Dispatchers.IO)

    override suspend fun syncSuppliers(): Result<Unit> = runCatching {
        val remoteSuppliers = suppliersDataSource.getAllSuppliers()
        val supplierEntities = remoteSuppliers.map { it.toEntity() }
        supplierDao.upsertSuppliers(supplierEntities)
    }
}