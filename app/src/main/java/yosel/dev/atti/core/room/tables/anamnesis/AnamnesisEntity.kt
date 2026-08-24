package yosel.dev.atti.core.room.tables.anamnesis

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity

@Entity(
    tableName = "anamnesis",
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
            childColumns = ["food_brand_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["food_unit_type_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["consultation_id"], name = "idx_anamnesis_consultation_id"),
        Index(value = ["food_brand_id"]),
        Index(value = ["food_unit_type_id"]),
        Index(value = ["status"], name = "idx_anamnesis_status")
    ]
)
data class AnamnesisEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID de Supabase
    @ColumnInfo(name = "consultation_id")
    val consultationId: String,
    @ColumnInfo(name = "has_outdoor_access")
    val hasOutdoorAccess: Boolean = false,
    @ColumnInfo(name = "housemates")
    val housemates: String = "",
    @ColumnInfo(name = "food_brand_id")
    val foodBrandId: Int = 0,
    @ColumnInfo(name = "food_quantity")
    val foodQuantity: Double = 0.0,
    @ColumnInfo(name = "food_unit_type_id")
    val foodUnitTypeId: Int = 0,
    @ColumnInfo(name = "homemade_food")
    val homemadeFood: String = "",
    @ColumnInfo(name = "feeding_frequency")
    val feedingFrequency: String = "",
    @ColumnInfo(name = "water_consumption")
    val waterConsumption: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
