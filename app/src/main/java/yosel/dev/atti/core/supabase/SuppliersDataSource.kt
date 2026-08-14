package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.SupplierDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class SuppliersDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun getAllSuppliers(): List<SupplierDto> {
        return postgrest.from(Constants.SUPPLIERS_SUPABASE)
            .select {
                order("status", Order.ASCENDING)
                order("created_at", Order.DESCENDING)
            }
            .decodeList<SupplierDto>()
    }

    suspend fun getSuppliersActives(): List<SupplierDto> {
        return postgrest.from(Constants.SUPPLIERS_SUPABASE)
            .select {
                filter {
                    eq("status", Constants.ACTIVE_STATUS)
                }
                order(column = "name", order = Order.ASCENDING)
            }
            .decodeList<SupplierDto>()
    }

    suspend fun insertAndGetSupplier(supplier: SupplierDto): SupplierDto {
        return postgrest.from(Constants.SUPPLIERS_SUPABASE)
            .insert(supplier) {
                select()
            }
            .decodeSingle<SupplierDto>()
    }

    suspend fun updateSupplier(supplier: SupplierDto) {
        postgrest.from(Constants.SUPPLIERS_SUPABASE)
            .update(supplier) {
                filter {
                    eq("id", supplier.id ?: "")
                }
            }
    }

    suspend fun updateSupplierStatus(supplierId: String, newStatus: Int){
        postgrest.from(Constants.SUPPLIERS_SUPABASE)
            .update(
                {
                    set("status", newStatus)
                }
            ) {
                filter {
                    eq("id", supplierId)
                }
            }
    }
}