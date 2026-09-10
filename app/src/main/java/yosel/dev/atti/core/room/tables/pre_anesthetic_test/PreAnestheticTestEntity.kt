package yosel.dev.atti.core.room.tables.pre_anesthetic_test

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity
import yosel.dev.atti.core.room.tables.product.ProductEntity
import yosel.dev.atti.core.room.tables.service.ServiceEntity

@Entity(
    tableName = "pre_anesthetic_tests",
    foreignKeys = [
        ForeignKey(
            entity = ConsultationEntity::class,
            parentColumns = ["id"],
            childColumns = ["consultation_id"],
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
        Index(value = ["consultation_id"], name = "idx_pre_anesthetic_tests_consultation_id"),
        Index(value = ["product_id"], name = "idx_pre_anesthetic_tests_product_id"),
        Index(value = ["service_id"], name = "idx_pre_anesthetic_tests_service_id"),
        Index(value = ["status"], name = "idx_pre_anesthetic_tests_status")
    ]
)
data class PreAnestheticTestEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "consultation_id")
    val consultationId: String,
    @ColumnInfo(name = "product_id")
    val productId: String? = null,
    @ColumnInfo(name = "service_id")
    val serviceId: String? = null,
    @ColumnInfo(name = "quantity")
    val quantity: Double = 1.0,
    @ColumnInfo(name = "notes")
    val notes: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
