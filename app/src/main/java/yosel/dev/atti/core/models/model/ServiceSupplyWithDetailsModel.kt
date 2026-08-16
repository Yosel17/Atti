package yosel.dev.atti.core.models.model

data class ServiceSupplyWithDetailsModel(
    val supply: ServiceSupplyModel = ServiceSupplyModel(),
    val product: ProductWithDetailsModel = ProductWithDetailsModel()
)
