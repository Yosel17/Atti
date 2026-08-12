package yosel.dev.atti.screens.add_supplier.domain

import yosel.dev.atti.core.models.model.SupplierModel

interface AddSupplierRepository {

    suspend fun insertSupplier(supplier: SupplierModel): Result<Unit>
}