package yosel.dev.atti.core.room.tables.receipt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity

@Entity(
    tableName = "receipts",
    foreignKeys = [
        ForeignKey(
            entity = ConsultationEntity::class,
            parentColumns = ["id"],
            childColumns = ["consultation_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["consultation_id"], name = "idx_receipts_consultation_id"),
        Index(value = ["status"], name = "idx_receipts_status"),
        Index(value = ["created_at"], name = "idx_receipts_created_at")
    ]
)
data class ReceiptEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, //UUID generado por Supabase
    @ColumnInfo(name = "receipt_number")
    val receiptNumber: Long,
    @ColumnInfo(name = "consultation_id")
    val consultationId: String? = null,
    @ColumnInfo(name = "customer_name")
    val customerName: String = "",
    @ColumnInfo(name = "subtotal")
    val subtotal: Double = 0.0,
    @ColumnInfo(name = "discount")
    val discount: Double = 0.0,
    @ColumnInfo(name = "tax")
    val tax: Double = 0.0,
    @ColumnInfo(name = "total")
    val total: Double = 0.0,
    @ColumnInfo(name = "notes")
    val notes: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)