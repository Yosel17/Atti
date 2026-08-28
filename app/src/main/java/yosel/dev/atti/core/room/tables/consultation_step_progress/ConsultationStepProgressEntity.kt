package yosel.dev.atti.core.room.tables.consultation_step_progress

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "consultation_step_progress",
    primaryKeys = ["consultation_id", "step_catalog_id"],
    indices = [
        Index(value = ["consultation_id"]),
        Index(value = ["consultation_id", "step_catalog_id"])
    ]
)
data class ConsultationStepProgressEntity(
    @ColumnInfo(name = "consultation_id")
    val consultationId: String,
    @ColumnInfo(name = "step_catalog_id")
    val stepCatalogId: Int,
    @ColumnInfo(name = "record_id")
    val recordId: String? = null,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "status")
    val status: Int = 1
)
