package yosel.dev.atti.core.room.tables.physiological_constants

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class PhysiologicalConstantsWithDetailsEntity(
    @Embedded val constants: PhysiologicalConstsEntity,
    @Relation(
        parentColumn = "weight_unit_catalog_id",
        entityColumn = "id"
    )
    val weightUnit: AppCatalogEntity?
)
