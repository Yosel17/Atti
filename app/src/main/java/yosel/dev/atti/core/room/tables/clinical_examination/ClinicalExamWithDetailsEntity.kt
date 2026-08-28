package yosel.dev.atti.core.room.tables.clinical_examination

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class ClinicalExamWithDetailsEntity(
    @Embedded val clinicalExam: ClinicalExaminationEntity,
    @Relation(
        parentColumn = "coat_catalog_id",
        entityColumn = "id"
    )
    val coat: AppCatalogEntity?,
    @Relation(
        entity = ClinicalExamLymphNodeEntity::class,
        parentColumn = "id",
        entityColumn = "clinical_examination_id"
    )
    val lymphNodes: List<ClinicalExamLymphNodeWithDetailsEntity> = emptyList()
)
