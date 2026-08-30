package yosel.dev.atti.core.room.tables.diagnosis

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class DiagnosisWithDetailsEntity(
    @Embedded val diagnosis: DiagnosisEntity,
    @Relation(
        parentColumn = "diagnosis_catalog_id",
        entityColumn = "id"
    )
    val catalog: AppCatalogEntity?
)
