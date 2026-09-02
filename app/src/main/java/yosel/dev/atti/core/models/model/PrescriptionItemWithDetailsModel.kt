package yosel.dev.atti.core.models.model

data class PrescriptionItemWithDetailsModel(
    val item: PrescriptionItemModel = PrescriptionItemModel(),
    val product: ProductWithDetailsModel? = null
) {
    // Nombre a mostrar en la tarjeta de la interfaz
    val displayName: String
        get() = if (item.isCustomProduct) {
            item.customProductName.ifBlank { "Medicamento sin nombre" }
        } else {
            product?.product?.commercialName ?: "Medicamento del catálogo"
        }

    // Subtítulo de categoría (solo si viene de catálogo)
    val categoryName: String?
        get() = product?.category?.name?.takeIf { it.isNotBlank() }
}
