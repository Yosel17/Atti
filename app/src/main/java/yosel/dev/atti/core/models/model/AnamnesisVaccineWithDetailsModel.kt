package yosel.dev.atti.core.models.model

data class AnamnesisVaccineWithDetailsModel(
    val vaccineEntry: AnamnesisVaccineModel = AnamnesisVaccineModel(),
    val vaccine: AppCatalogModel = AppCatalogModel(),
    val scheme: AppCatalogModel = AppCatalogModel()
)
