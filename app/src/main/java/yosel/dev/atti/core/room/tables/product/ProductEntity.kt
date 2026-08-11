package yosel.dev.atti.core.room.tables.product

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.supplier.SupplierEntity

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = SupplierEntity::class,
            parentColumns = ["id"],
            childColumns = ["supplier_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["unit_type_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["supplier_id"]),
        Index(value = ["category_id"]),
        Index(value = ["unit_type_id"]),
        Index(value = ["status"]),
        Index(value = ["created_at"])
    ]
)
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID de Supabase
    @ColumnInfo(name = "supplier_id")
    val supplierId: String? = null,
    @ColumnInfo(name = "category_id")
    val categoryId: Int = 0,
    @ColumnInfo(name = "unit_type_id")
    val unitTypeId: Int = 0,
    @ColumnInfo(name = "commercial_name")
    val commercialName: String,
    @ColumnInfo(name = "brand")
    val brand: String = "",
    @ColumnInfo(name = "purchase_price")
    val purchasePrice: Double = 0.0,
    @ColumnInfo(name = "sale_price")
    val salePrice: Double = 0.0,
    @ColumnInfo(name = "stock")
    val stock: Double = 0.0,
    @ColumnInfo(name = "min_stock")
    val minStock: Double = 0.0,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int
)
