package yosel.dev.atti.core.models.model

data class PatientWithDetailsModel(
    val patient: PatientModel = PatientModel(),
    val species: AppCatalogModel = AppCatalogModel(),
    val gender: AppCatalogModel = AppCatalogModel(),
    val client: ClientModel = ClientModel()
)
