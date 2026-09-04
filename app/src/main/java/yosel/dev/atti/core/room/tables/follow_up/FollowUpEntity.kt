package yosel.dev.atti.core.room.tables.follow_up

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity
import yosel.dev.atti.core.room.tables.patient.PatientEntity

@Entity(
    tableName = "follow_ups",
    foreignKeys = [
        ForeignKey(
            entity = ConsultationEntity::class,
            parentColumns = ["id"],
            childColumns = ["consultation_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["consultation_id"], name = "idx_follow_ups_consultation_id"),
        Index(value = ["patient_id"], name = "idx_follow_ups_patient_id"),
        Index(value = ["scheduled_at"], name = "idx_follow_ups_scheduled_at"),
        Index(value = ["status"], name = "idx_follow_ups_status")
    ]
)
data class FollowUpEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID generado por Supabase / PostgreSQL
    @ColumnInfo(name = "consultation_id")
    val consultationId: String,
    @ColumnInfo(name = "patient_id")
    val patientId: String,
    @ColumnInfo(name = "scheduled_at")
    val scheduledAt: String, // Formato ISO-8601 (permite ordenación lexicográfica en SQLite)
    @ColumnInfo(name = "reason")
    val reason: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
