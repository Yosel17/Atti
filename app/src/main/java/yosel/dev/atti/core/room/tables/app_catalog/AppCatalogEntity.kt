package yosel.dev.atti.core.room.tables.app_catalog

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "app_catalogs",
    indices = [
        Index(value = ["catalog_type_id"], name = "idx_catalogs_type_id"),
    ]
)
data class AppCatalogEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "catalog_type_id")
    val catalogTypeId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "is_active")
    val isActive: Boolean,

    @ColumnInfo(name = "created_at")
    val createdAt: String
)
