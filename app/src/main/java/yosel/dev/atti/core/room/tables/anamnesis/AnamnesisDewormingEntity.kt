package yosel.dev.atti.core.room.tables.anamnesis

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

@Entity(
    tableName = "anamnesis_dewormings",
    foreignKeys = [
        ForeignKey(
            entity = AnamnesisEntity::class,
            parentColumns = ["id"],
            childColumns = ["anamnesis_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_catalog_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["anamnesis_id"], name = "idx_anamnesis_dewormings_anamnesis_id"),
        Index(value = ["product_catalog_id"])
    ]
)
data class AnamnesisDewormingEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "anamnesis_id")
    val anamnesisId: String,
    @ColumnInfo(name = "application_date")
    val applicationDate: String = "",
    @ColumnInfo(name = "deworming_type")
    val dewormingType: String,
    @ColumnInfo(name = "product_catalog_id")
    val productCatalogId: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: String = ""
)
