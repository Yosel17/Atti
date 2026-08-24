package yosel.dev.atti.core.room.tables.anamnesis

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class AnamnesisDewormingWithDetailsEntity(
    @Embedded val deworming: AnamnesisDewormingEntity,
    @Relation(
        parentColumn = "product_catalog_id",
        entityColumn = "id"
    )
    val product: AppCatalogEntity?
)
