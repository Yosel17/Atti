package yosel.dev.atti.core.models.model

data class FastingWithDetailsModel(
    val fasting: FastingModel = FastingModel(),
    val foodFasting: AppCatalogModel = AppCatalogModel(),
    val waterFasting: AppCatalogModel = AppCatalogModel()
)
