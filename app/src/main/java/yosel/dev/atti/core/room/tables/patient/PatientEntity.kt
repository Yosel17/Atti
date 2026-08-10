package yosel.dev.atti.core.room.tables.patient

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.client.ClientEntity

@Entity(
    tableName = "patients",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["client_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["client_id"]),Index(value = ["created_at"])]
)
data class PatientEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID de Supabase/PostgreSQL

    @ColumnInfo(name = "client_id")
    val clientId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "species_id")
    val speciesId: Int = 0,

    @ColumnInfo(name = "gender_id")
    val genderId: Int = 0,

    @ColumnInfo(name = "breed")
    val breed: String = "",

    @ColumnInfo(name = "age_years")
    val ageYears: Int = 0,

    @ColumnInfo(name = "age_months")
    val ageMonths: Int = 0,

    @ColumnInfo(name = "color")
    val color: String = "",

    @ColumnInfo(name = "is_neutered")
    val isNeutered: Boolean = false,

    @ColumnInfo(name = "photo_url")
    val photoUrl: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: String = "",

    @ColumnInfo(name = "status")
    val status: Int
)
