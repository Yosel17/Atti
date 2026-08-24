package yosel.dev.atti.core.models.model

data class AnamnesisWithDetailsModel(
    val anamnesis: AnamnesisModel = AnamnesisModel(),
    val foodBrand: AppCatalogModel = AppCatalogModel(),
    val foodUnit: AppCatalogModel = AppCatalogModel(),
    val environmentOptions: List<AnamnesisEnviOptWithDetailsModel> = emptyList(),
    val vaccines: List<AnamnesisVaccineWithDetailsModel> = emptyList(),
    val dewormings: List<AnamnesisDewormingWithDetailsModel> = emptyList()
)
