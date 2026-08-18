package yosel.dev.atti.core.models.request

import kotlinx.serialization.Serializable
import yosel.dev.atti.core.models.dto.ServiceDto
import yosel.dev.atti.core.models.dto.ServiceSupplyDto

@Serializable
data class CreateServiceRequest(
    val service_data: ServiceDto,
    val supplies_data: List<ServiceSupplyDto>
)
