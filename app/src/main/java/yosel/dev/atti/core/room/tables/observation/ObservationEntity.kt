package yosel.dev.atti.core.room.tables.observation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity

@Entity(
    tableName = "observations",
    foreignKeys = [
        ForeignKey(
            entity = ConsultationEntity::class,
            parentColumns = ["id"],
            childColumns = ["consultation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["consultation_id"], name = "idx_observations_consultation_id"),
        Index(value = ["status"], name = "idx_observations_status")
    ]
)
data class ObservationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID de Supabase / PostgreSQL
    @ColumnInfo(name = "consultation_id")
    val consultationId: String,
    @ColumnInfo(name = "observation")
    val observation: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
