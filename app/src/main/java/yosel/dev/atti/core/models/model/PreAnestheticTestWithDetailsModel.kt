package yosel.dev.atti.core.models.model

data class PreAnestheticTestWithDetailsModel(
    val preAnestheticTest: PreAnestheticTestModel = PreAnestheticTestModel(),
    val product: ProductWithDetailsModel? = null,
    val service: ServiceWithDetailsModel? = null
) {
    val itemName: String
        get() = when {
            preAnestheticTest.isProduct -> product?.product?.commercialName ?: "Producto sin nombre"
            preAnestheticTest.isService -> service?.service?.name ?: "Servicio sin nombre"
            else -> "Ítem desconocido"
        }

    val unitPrice: Double
        get() = when {
            preAnestheticTest.isProduct -> product?.product?.salePrice ?: 0.0
            preAnestheticTest.isService -> service?.service?.salePrice ?: 0.0
            else -> 0.0
        }

    val subtotal: Double
        get() = unitPrice * preAnestheticTest.quantity
}
