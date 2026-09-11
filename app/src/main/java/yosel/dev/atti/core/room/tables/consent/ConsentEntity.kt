package yosel.dev.atti.core.room.tables.consent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity

@Entity(
    tableName = "consents",
    foreignKeys = [
        ForeignKey(
            entity = ConsultationEntity::class,
            parentColumns = ["id"],
            childColumns = ["consultation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["consultation_id"], name = "idx_consents_consultation_id"),
        Index(value = ["status"], name = "idx_consents_status")
    ]
)
data class ConsentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID generado por Supabase / PostgreSQL
    @ColumnInfo(name = "consultation_id")
    val consultationId: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
