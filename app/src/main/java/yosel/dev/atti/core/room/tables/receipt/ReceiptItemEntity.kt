package yosel.dev.atti.core.room.tables.receipt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.product.ProductEntity
import yosel.dev.atti.core.room.tables.service.ServiceEntity

@Entity(
    tableName = "receipt_items",
    foreignKeys = [
        ForeignKey(
            entity = ReceiptEntity::class,
            parentColumns = ["id"],
            childColumns = ["receipt_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["service_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["receipt_id"], name = "idx_receipt_items_receipt_id"),
        Index(value = ["product_id"], name = "idx_receipt_items_product_id"),
        Index(value = ["service_id"], name = "idx_receipt_items_service_id"),
        Index(value = ["status"], name = "idx_receipt_items_status")
    ]
)
data class ReceiptItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID generado por Supabase
    @ColumnInfo(name = "receipt_id")
    val receiptId: String,
    @ColumnInfo(name = "product_id")
    val productId: String? = null,
    @ColumnInfo(name = "service_id")
    val serviceId: String? = null,
    @ColumnInfo(name = "quantity")
    val quantity: Double = 1.0,
    @ColumnInfo(name = "unit_price")
    val unitPrice: Double = 0.0,
    @ColumnInfo(name = "subtotal")
    val subtotal: Double = 0.0,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
