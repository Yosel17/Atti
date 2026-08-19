package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.ServiceSupplyDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class ServiceSuppliesDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getSuppliesWithProductDetailsByServiceId(serviceId: String): List<ServiceSupplyDto> {
        return postgrest.from(Constants.SERVICE_SUPPLIES_SUPABASE)
            .select(
                columns = Columns.raw(
                    value = """
                        *,
                        product:products!product_id(
                            *,
                            supplier:suppliers!supplier_id(*),
                            category:app_catalogs!category_id(*),
                            unit_type:app_catalogs!unit_type_id(*)
                        )
                    """.trimIndent()
                )
            ) {
                filter {
                    eq("service_id", serviceId)
                }
                order("id", Order.ASCENDING)
            }
            .decodeList<ServiceSupplyDto>()
    }

    suspend fun insertAndGetSupply(supply: ServiceSupplyDto): ServiceSupplyDto {
        return postgrest.from(Constants.SERVICE_SUPPLIES_SUPABASE)
            .insert(supply) {
                select()
            }
            .decodeSingle<ServiceSupplyDto>()
    }

    suspend fun insertAndGetSupplies(supplies: List<ServiceSupplyDto>): List<ServiceSupplyDto> {
        if (supplies.isEmpty()) return emptyList()
        return postgrest.from(Constants.SERVICE_SUPPLIES_SUPABASE)
            .insert(supplies) {
                select()
            }
            .decodeList<ServiceSupplyDto>()
    }

    suspend fun updateSupply(supply: ServiceSupplyDto) {
        postgrest.from(Constants.SERVICE_SUPPLIES_SUPABASE)
            .update(supply) {
                filter {
                    eq("id", supply.id ?: 0)
                }
            }
    }

    suspend fun updateSupplyStatus(supplyId: Int, newStatus: Int) {
        postgrest.from(Constants.SERVICE_SUPPLIES_SUPABASE)
            .update(
                {
                    set("status", newStatus)
                }
            ) {
                filter {
                    eq("id", supplyId)
                }
            }
    }

    suspend fun deleteSuppliesByServiceId(serviceId: String) {
        postgrest.from(Constants.SERVICE_SUPPLIES_SUPABASE)
            .delete {
                filter {
                    eq("service_id", serviceId)
                }
            }
    }
}