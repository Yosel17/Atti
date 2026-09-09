package yosel.dev.atti.core.room.tables.asa_classification

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class AsaClassificationWithDetailsEntity(
    @Embedded val asaClassification: AsaClassificationEntity,
    @Relation(
        parentColumn = "asa_catalog_id",
        entityColumn = "id"
    )
    val catalog: AppCatalogEntity?
)
