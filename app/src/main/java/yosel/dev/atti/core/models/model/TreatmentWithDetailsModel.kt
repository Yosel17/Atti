package yosel.dev.atti.core.models.model

data class TreatmentWithDetailsModel(
    val treatment: TreatmentModel = TreatmentModel(),
    val product: ProductWithDetailsModel? = null,
    val service: ServiceWithDetailsModel? = null
) {
    val itemName: String
        get() = when {
            treatment.isProduct -> product?.product?.commercialName ?: "Producto sin nombre"
            treatment.isService -> service?.service?.name ?: "Servicio sin nombre"
            else -> "Ítem desconocido"
        }

    val unitPrice: Double
        get() = when {
            treatment.isProduct -> product?.product?.salePrice ?: 0.0
            treatment.isService -> service?.service?.salePrice ?: 0.0
            else -> 0.0
        }

    val subtotal: Double
        get() = unitPrice * treatment.quantity
}
