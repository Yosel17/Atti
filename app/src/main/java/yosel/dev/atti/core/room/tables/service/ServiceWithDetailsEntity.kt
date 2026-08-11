package yosel.dev.atti.core.room.tables.service

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class ServiceWithDetailsEntity(
    @Embedded val service: ServiceEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: AppCatalogEntity?
)
