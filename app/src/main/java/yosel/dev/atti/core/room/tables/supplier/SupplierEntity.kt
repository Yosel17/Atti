package yosel.dev.atti.core.room.tables.supplier

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "suppliers",
    indices = [
        Index(value = ["status"]),
        Index(value = ["created_at"])
    ]
)
data class SupplierEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID de Supabase
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "tax_id")
    val taxId: String = "",
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String = "",
    @ColumnInfo(name = "address")
    val address: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int
)
