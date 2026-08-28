package yosel.dev.atti.core.room.tables.clinical_examination

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity

@Entity(
    tableName = "clinical_examinations",
    foreignKeys = [
        ForeignKey(
            entity = ConsultationEntity::class,
            parentColumns = ["id"],
            childColumns = ["consultation_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["coat_catalog_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["consultation_id"], name = "idx_clinical_examinations_consultation_id"),
        Index(value = ["coat_catalog_id"]),
        Index(value = ["status"], name = "idx_clinical_examinations_status")
    ]
)
data class ClinicalExaminationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "consultation_id")
    val consultationId: String,
    @ColumnInfo(name = "mucous_membranes")
    val mucousMembranes: String = "",
    @ColumnInfo(name = "coat_catalog_id")
    val coatCatalogId: Int? = null,
    @ColumnInfo(name = "abdominal_palpation")
    val abdominalPalpation: String = "",
    @ColumnInfo(name = "body_condition")
    val bodyCondition: Int = 3,
    @ColumnInfo(name = "other_findings")
    val otherFindings: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
