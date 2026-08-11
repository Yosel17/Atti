package yosel.dev.atti.core.models.model

data class ProductWithDetailsModel(
    val product: ProductModel = ProductModel(),
    val supplier: SupplierModel? = null,
    val category: AppCatalogModel = AppCatalogModel(),
    val unitType: AppCatalogModel = AppCatalogModel()
)
