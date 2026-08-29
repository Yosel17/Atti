package yosel.dev.atti.core.models.model

data class PhysiologicalConstsWithDetailsModel(
    val constants: PhysiologicalConstsModel = PhysiologicalConstsModel(),
    val weightUnit: AppCatalogModel = AppCatalogModel()
)
