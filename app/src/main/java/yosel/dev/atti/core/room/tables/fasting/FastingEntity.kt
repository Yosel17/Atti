package yosel.dev.atti.core.room.tables.fasting

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity

@Entity(
    tableName = "fasting",
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
            childColumns = ["food_fasting_catalog_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["water_fasting_catalog_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["consultation_id"], unique = true, name = "idx_fasting_consultation_id"),
        Index(value = ["food_fasting_catalog_id"], name = "idx_fasting_food_catalog_id"),
        Index(value = ["water_fasting_catalog_id"], name = "idx_fasting_water_catalog_id"),
        Index(value = ["status"], name = "idx_fasting_status")
    ]
)
data class FastingEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID generado por Supabase/PostgreSQL
    @ColumnInfo(name = "consultation_id")
    val consultationId: String,
    @ColumnInfo(name = "food_fasting_catalog_id")
    val foodFastingCatalogId: Int,
    @ColumnInfo(name = "water_fasting_catalog_id")
    val waterFastingCatalogId: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
