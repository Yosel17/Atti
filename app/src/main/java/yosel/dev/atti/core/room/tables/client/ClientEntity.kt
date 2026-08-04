package yosel.dev.atti.core.room.tables.client

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID generado por Supabase/PostgreSQL

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "document_id")
    val documentId: String = "",

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String = "",

    @ColumnInfo(name = "email")
    val email: String = "",

    @ColumnInfo(name = "address")
    val address: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: String = ""
)