package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import yosel.dev.atti.core.models.dto.ServiceDto
import yosel.dev.atti.core.models.request.CreateServiceRequest
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

    suspend fun updateService(service: ServiceDto) {
        postgrest.from(Constants.SERVICES_SUPABASE)
            .update(service) {
                filter {
                    eq("id", service.id ?: "")
                }
            }
    }

    suspend fun updateServiceStatus(serviceId: String, newStatus: Int){
        postgrest.from(Constants.SERVICES_SUPABASE)
            .update(
                {
                    set("status", newStatus)
                }
            ) {
                filter {
                    eq("id", serviceId)
                }
            }
    }

    suspend fun insertServiceWithSupplies(request: CreateServiceRequest): ServiceDto {
        // Esto ejecuta la función en PostgreSQL que acabamos de crear
        return postgrest.rpc(
            function = "create_service_with_supplies",
            parameters = request
        ).decodeAs<ServiceDto>()
    }
}