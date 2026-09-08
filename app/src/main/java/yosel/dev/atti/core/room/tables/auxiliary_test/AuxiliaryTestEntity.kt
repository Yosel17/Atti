package yosel.dev.atti.core.room.tables.auxiliary_test

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity

@Entity(
    tableName = "auxiliary_tests",
    foreignKeys = [
        ForeignKey(
            entity = ConsultationEntity::class,
            parentColumns = ["id"],
            childColumns = ["consultation_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AppCatalogEntity::class,
            parentColumns = ["id"],
            childColumns = ["test_catalog_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["consultation_id"], name = "idx_auxiliary_tests_consultation_id"),
        Index(value = ["test_catalog_id"], name = "idx_auxiliary_tests_catalog_id"),
        Index(value = ["status"], name = "idx_auxiliary_tests_status"),
        Index(value = ["consultation_id", "test_catalog_id"], unique = true)
    ]
)
data class AuxiliaryTestEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // UUID generado por Supabase/PostgreSQL
    @ColumnInfo(name = "consultation_id")
    val consultationId: String,
    @ColumnInfo(name = "test_catalog_id")
    val testCatalogId: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "status")
    val status: Int = 1
)
