package yosel.dev.atti.core.models.model

data class ReceiptItemWithDetailsModel(
    val item: ReceiptItemModel = ReceiptItemModel(),
    val product: ProductWithDetailsModel? = null,
    val service: ServiceWithDetailsModel? = null
) {
    val itemName: String
        get() = when {
            item.isProduct -> product?.product?.commercialName ?: "Producto sin nombre"
            item.isService -> service?.service?.name ?: "Servicio sin nombre"
            else -> "Ítem desconocido"
        }
}
