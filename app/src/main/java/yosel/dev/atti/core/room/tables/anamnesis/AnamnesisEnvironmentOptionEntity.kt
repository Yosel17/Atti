package yosel.dev.atti.core.room.tables.anamnesis

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

@Entity(
    tableName = "anamnesis_environment_options",
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
            childColumns = ["catalog_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["anamnesis_id"], name = "idx_anamnesis_env_anamnesis_id"),
        Index(value = ["catalog_id"]),
        Index(value = ["anamnesis_id", "catalog_id"], unique = true)
    ]
)
data class AnamnesisEnvironmentOptionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "anamnesis_id")
    val anamnesisId: String,
    @ColumnInfo(name = "catalog_id")
    val catalogId: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: String = ""
)
