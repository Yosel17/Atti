package yosel.dev.atti.core.models.model

data class ServiceWithDetailsModel(
    val service: ServiceModel = ServiceModel(),
    val category: AppCatalogModel = AppCatalogModel(),
    val supplies: List<ServiceSupplyWithDetailsModel> = emptyList()
)
