package yosel.dev.atti.core.models.model

data class AuxiliaryTestWithDetailsModel(
    val auxiliaryTest: AuxiliaryTestModel = AuxiliaryTestModel(),
    val catalog: AppCatalogModel = AppCatalogModel()
)
