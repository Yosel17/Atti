package yosel.dev.atti.core.room.tables.anamnesis

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class AnamnesisVaccineWithDetailsEntity(
    @Embedded val vaccineEntry: AnamnesisVaccineEntity,
    @Relation(
        parentColumn = "vaccine_catalog_id",
        entityColumn = "id"
    )
    val vaccine: AppCatalogEntity?,
    @Relation(
        parentColumn = "scheme_catalog_id",
        entityColumn = "id"
    )
    val scheme: AppCatalogEntity?
)
