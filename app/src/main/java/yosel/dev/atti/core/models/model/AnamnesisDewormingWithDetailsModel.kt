package yosel.dev.atti.core.models.model

data class AnamnesisDewormingWithDetailsModel(
    val deworming: AnamnesisDewormingModel = AnamnesisDewormingModel(),
    val product: AppCatalogModel = AppCatalogModel()
)
