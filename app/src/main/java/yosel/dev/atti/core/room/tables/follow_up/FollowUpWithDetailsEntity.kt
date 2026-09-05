package yosel.dev.atti.core.room.tables.follow_up

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.consultation.ConsultationEntity
import yosel.dev.atti.core.room.tables.consultation.ConsultationWithDetailsEntity
import yosel.dev.atti.core.room.tables.patient.PatientEntity
import yosel.dev.atti.core.room.tables.patient.PatientWithDetailsEntity

data class FollowUpWithDetailsEntity(
    @Embedded val followUp: FollowUpEntity,
    @Relation(
        entity = PatientEntity::class,
        parentColumn = "patient_id",
        entityColumn = "id"
    )
    val patientWithDetails: PatientWithDetailsEntity?,
    @Relation(
        entity = ConsultationEntity::class,
        parentColumn = "consultation_id",
        entityColumn = "id"
    )
    val consultationWithDetails: ConsultationWithDetailsEntity?
)
