package yosel.dev.atti.core.models.model

data class ShiftMedicationWithDetailsModel(
    val shiftMedication: ShiftMedicationModel = ShiftMedicationModel(),
    val product: ProductWithDetailsModel? = null,
    val service: ServiceWithDetailsModel? = null
) {
    val itemName: String
        get() = when {
            shiftMedication.isProduct -> product?.product?.commercialName ?: "Producto sin nombre"
            shiftMedication.isService -> service?.service?.name ?: "Servicio sin nombre"
            else -> "Ítem desconocido"
        }

    val unitPrice: Double
        get() = when {
            shiftMedication.isProduct -> product?.product?.salePrice ?: 0.0
            shiftMedication.isService -> service?.service?.salePrice ?: 0.0
            else -> 0.0
        }

    val subtotal: Double
        get() = unitPrice * shiftMedication.quantity
}
