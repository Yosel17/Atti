package yosel.dev.atti.core.room.tables.prescription

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.product.ProductEntity

@Entity(
    tableName = "prescription_items",
    foreignKeys = [
        ForeignKey(
            entity = PrescriptionEntity::class,
            parentColumns = ["id"],
            childColumns = ["prescription_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["prescription_id"], name = "idx_prescription_items_prescription_id"),
        Index(value = ["product_id"], name = "idx_prescription_items_product_id"),
        Index(value = ["status"], name = "idx_prescription_items_status")
    ]
)
data class PrescriptionItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID generado por Supabase/PostgreSQL
    @ColumnInfo(name = "prescription_id")
    val prescriptionId: String,
    @ColumnInfo(name = "product_id")
    val productId: String? = null,
    @ColumnInfo(name = "custom_product_name")
    val customProductName: String = "",
    @ColumnInfo(name = "instructions")
    val instructions: String,
    @ColumnInfo(name = "quantity")
    val quantity: Double = 1.0,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
