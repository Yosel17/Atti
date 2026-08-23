package yosel.dev.atti.core.room.tables.consultation_type_step

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

@Entity(
    tableName = "consultation_type_steps",
    foreignKeys = [
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["consultation_type_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["step_catalog_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["consultation_type_id"], name = "idx_type_steps_type"),
        Index(value = ["step_catalog_id"]),
        Index(value = ["consultation_type_id", "step_catalog_id"], unique = true)
    ]
)
data class ConsultationTypeStepEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "consultation_type_id")
    val consultationTypeId: Int,
    @ColumnInfo(name = "step_catalog_id")
    val stepCatalogId: Int,
    @ColumnInfo(name = "step_order")
    val stepOrder: Int = 1,
    @ColumnInfo(name = "is_required")
    val isRequired: Boolean = true
)
