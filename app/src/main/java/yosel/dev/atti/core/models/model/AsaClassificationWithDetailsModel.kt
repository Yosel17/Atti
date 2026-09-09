package yosel.dev.atti.core.models.model

data class AsaClassificationWithDetailsModel(
    val asaClassification: AsaClassificationModel = AsaClassificationModel(),
    val catalog: AppCatalogModel = AppCatalogModel()
)
