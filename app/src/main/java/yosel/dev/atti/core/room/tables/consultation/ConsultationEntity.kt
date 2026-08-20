package yosel.dev.atti.core.room.tables.consultation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.patient.PatientEntity

@Entity(
    tableName = "consultations",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["consultation_type_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["patient_id"], name = "idx_consultations_patient_id"),
        Index(value = ["status"], name = "idx_consultations_status"),
        Index(value = ["patient_id", "status"], name = "idx_consultations_patient_status"),
        Index(value = ["consultation_type_id"])
    ]
)
data class ConsultationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID generado por Supabase/PostgreSQL
    @ColumnInfo(name = "patient_id")
    val patientId: String,
    @ColumnInfo(name = "consultation_type_id")
    val consultationTypeId: Int = 0,
    @ColumnInfo(name = "started_at")
    val startedAt: String = "",
    @ColumnInfo(name = "completed_at")
    val completedAt: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
