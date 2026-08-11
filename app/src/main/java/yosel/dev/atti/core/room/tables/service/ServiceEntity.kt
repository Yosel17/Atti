package yosel.dev.atti.core.room.tables.service

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

@Entity(
    tableName = "services",
    foreignKeys = [
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["status"]),
        Index(value = ["created_at"])
    ]
)
data class ServiceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID de Supabase
    @ColumnInfo(name = "category_id")
    val categoryId: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "sale_price")
    val salePrice: Double = 0.0,
    @ColumnInfo(name = "estimated_cost")
    val estimatedCost: Double = 0.0,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int
)
