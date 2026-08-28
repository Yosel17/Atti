package yosel.dev.atti.core.room.tables.clinical_examination

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class ClinicalExamLymphNodeWithDetailsEntity(
    @Embedded val lymphNode: ClinicalExamLymphNodeEntity,
    @Relation(
        parentColumn = "catalog_id",
        entityColumn = "id"
    )
    val catalog: AppCatalogEntity?
)
