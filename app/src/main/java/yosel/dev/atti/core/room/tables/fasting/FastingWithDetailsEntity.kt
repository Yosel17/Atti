package yosel.dev.atti.core.room.tables.fasting

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class FastingWithDetailsEntity(
    @Embedded val fasting: FastingEntity,
    @Relation(
        parentColumn = "food_fasting_catalog_id",
        entityColumn = "id"
    )
    val foodFasting: AppCatalogEntity?,
    @Relation(
        parentColumn = "water_fasting_catalog_id",
        entityColumn = "id"
    )
    val waterFasting: AppCatalogEntity?
)
