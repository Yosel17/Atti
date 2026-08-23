package yosel.dev.atti.core.room.tables.consultation_type_step

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class ConsultationTypeStepWithDetailsEntity(
    @Embedded val typeStep: ConsultationTypeStepEntity,
    @Relation(
        parentColumn = "consultation_type_id",
        entityColumn = "id"
    )
    val consultationType: AppCatalogEntity?,
    @Relation(
        parentColumn = "step_catalog_id",
        entityColumn = "id"
    )
    val stepCatalog: AppCatalogEntity?
)
