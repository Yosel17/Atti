package yosel.dev.atti.core.room.tables.physiological_constants

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity

@Entity(
    tableName = "physiological_consts",
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
            childColumns = ["weight_unit_catalog_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["consultation_id"], name = "idx_physio_constants_consultation_id"),
        Index(value = ["weight_unit_catalog_id"]),
        Index(value = ["status"], name = "idx_physio_constants_status")
    ]
)
data class PhysiologicalConstsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID de Supabase
    @ColumnInfo(name = "consultation_id")
    val consultationId: String,
    @ColumnInfo(name = "temperature")
    val temperature: Double? = null,
    @ColumnInfo(name = "heart_rate")
    val heartRate: Int? = null,
    @ColumnInfo(name = "respiratory_rate")
    val respiratoryRate: Int? = null,
    @ColumnInfo(name = "weight")
    val weight: Double? = null,
    @ColumnInfo(name = "weight_unit_catalog_id")
    val weightUnitCatalogId: Int? = null,
    @ColumnInfo(name = "capillary_refill_time")
    val capillaryRefillTime: Int? = null,
    @ColumnInfo(name = "skin_turgor")
    val skinTurgor: Int? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
