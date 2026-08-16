package yosel.dev.atti.core.room.tables.service_supply

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.product.ProductEntity
import yosel.dev.atti.core.room.tables.service.ServiceEntity

@Entity(
    tableName = "service_supplies",
    foreignKeys = [
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["service_id"],
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
        Index(value = ["service_id"]),
        Index(value = ["product_id"]),
        Index(value = ["status"]),
        Index(value = ["service_id", "product_id"], unique = true)
    ]
)
data class ServiceSupplyEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "service_id")
    val serviceId: String,
    @ColumnInfo(name = "product_id")
    val productId: String,
    @ColumnInfo(name = "quantity_required")
    val quantityRequired: Double = 1.0,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
