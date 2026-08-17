package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.ServiceDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class ServicesDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getAllServicesWithDetails(): List<ServiceDto> {
        return postgrest.from(Constants.SERVICES_SUPABASE)
            .select(
                columns = Columns.raw(
                    value = """
                    *,
                    category:app_catalogs!category_id(*),
                    supplies:service_supplies(
                        *,
                        product:products(
                            *,
                            supplier:suppliers(*),
                            category:app_catalogs!category_id(*),
                            unit_type:app_catalogs!unit_type_id(*)
                        )
                    )
                """.trimIndent()
                )
            ) {
                order("status", Order.ASCENDING)
                order("created_at", Order.DESCENDING)
            }
            .decodeList<ServiceDto>()
    }

    suspend fun insertServiceAndReturn(service: ServiceDto): ServiceDto {
        return postgrest.from(Constants.SERVICES_SUPABASE)
            .insert(service) {
                select()
            }
            .decodeSingle<ServiceDto>()
    }
}