package yosel.dev.atti.core.room.tables.client

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.patient.PatientEntity
import yosel.dev.atti.core.room.tables.patient.PatientWithDetailsEntity

data class ClientWithPatientsWithCatalogsEntity(
    @Embedded val client: ClientEntity,
    @Relation(
        entity = PatientEntity::class,
        parentColumn = "id",
        entityColumn = "client_id"
    )
    val patients: List<PatientWithDetailsEntity>
)
