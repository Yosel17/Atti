package yosel.dev.atti.core.models.model

data class ServiceWithDetailsModel(
    val service: ServiceModel = ServiceModel(),
    val category: AppCatalogModel = AppCatalogModel(),
    val supplies: List<ServiceSupplyWithDetailsModel> = emptyList()
){
    val totalCost: Double
        get() = if (supplies.isNotEmpty()) {
            supplies.sumOf { it.supply.quantityRequired * it.product.product.purchasePrice }
        } else {
            service.estimatedCost
        }

    val profitMarginPercentage: Int
        get() {
            val salePrice = service.salePrice
            val cost = totalCost
            return if (salePrice > 0.0) {
                (((salePrice - cost) / salePrice) * 100).toInt().coerceAtLeast(0)
            } else {
                0
            }
        }
}
