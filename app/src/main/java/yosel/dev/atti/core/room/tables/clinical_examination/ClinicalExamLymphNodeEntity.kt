package yosel.dev.atti.core.room.tables.clinical_examination

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

@Entity(
    tableName = "clinical_examination_lymph_nodes",
    foreignKeys = [
        ForeignKey(
            entity = ClinicalExaminationEntity::class,
            parentColumns = ["id"],
            childColumns = ["clinical_examination_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["catalog_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["clinical_examination_id"], name = "idx_clinical_exam_lymph_nodes_exam_id"),
        Index(value = ["catalog_id"]),
        Index(value = ["clinical_examination_id", "catalog_id"], unique = true)
    ]
)
data class ClinicalExamLymphNodeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "clinical_examination_id")
    val clinicalExaminationId: String,
    @ColumnInfo(name = "catalog_id")
    val catalogId: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: String = ""
)
