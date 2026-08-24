package yosel.dev.atti.core.models.model

data class AnamnesisEnviOptWithDetailsModel(
    val option: AnamnesisEnvironmentOptionModel = AnamnesisEnvironmentOptionModel(),
    val catalog: AppCatalogModel = AppCatalogModel()
)
