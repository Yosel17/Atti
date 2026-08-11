package yosel.dev.atti.core.models.model

data class PatientWithCatalogsModel(
    val patient: PatientModel = PatientModel(),
    val species: AppCatalogModel = AppCatalogModel(),
    val gender: AppCatalogModel = AppCatalogModel()
)
