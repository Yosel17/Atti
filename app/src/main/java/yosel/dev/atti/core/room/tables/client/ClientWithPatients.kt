package yosel.dev.atti.core.room.tables.client

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.patient.PatientEntity

data class ClientWithPatients(
    @Embedded val client: ClientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "client_id"
    )
    val patients: List<PatientEntity>
)
