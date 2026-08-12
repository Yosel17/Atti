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
}