package yosel.dev.atti.core.room.tables.consultation

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.patient.PatientEntity
import yosel.dev.atti.core.room.tables.patient.PatientWithDetailsEntity

data class ConsultationWithDetailsEntity(
    @Embedded val consultation: ConsultationEntity,
    @Relation(
        entity = PatientEntity::class,
        parentColumn = "patient_id",
        entityColumn = "id"
    )
    val patientWithDetails: PatientWithDetailsEntity?,
    @Relation(
        entity = AppCatalogEntity::class,
        parentColumn = "consultation_type_id",
        entityColumn = "id"
    )
    val consultationType: AppCatalogEntity?
)
