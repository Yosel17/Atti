package yosel.dev.atti.core.room.tables.anamnesis

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

@Entity(
    tableName = "anamnesis_vaccines",
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
            childColumns = ["vaccine_catalog_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["scheme_catalog_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["anamnesis_id"], name = "idx_anamnesis_vaccines_anamnesis_id"),
        Index(value = ["vaccine_catalog_id"]),
        Index(value = ["scheme_catalog_id"])
    ]
)
data class AnamnesisVaccineEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "anamnesis_id")
    val anamnesisId: String,
    @ColumnInfo(name = "application_date")
    val applicationDate: String = "",
    @ColumnInfo(name = "vaccine_catalog_id")
    val vaccineCatalogId: Int,
    @ColumnInfo(name = "scheme_catalog_id")
    val schemeCatalogId: Int = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: String = ""
)
