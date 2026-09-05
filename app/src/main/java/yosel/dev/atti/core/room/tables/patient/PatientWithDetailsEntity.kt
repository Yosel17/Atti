package yosel.dev.atti.core.room.tables.patient

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.client.ClientEntity

data class PatientWithDetailsEntity(
    @Embedded val patient: PatientEntity,

    @Relation(
        parentColumn = "species_id",
        entityColumn = "id"
    )
    val species: AppCatalogEntity?,

    @Relation(
        parentColumn = "gender_id",
        entityColumn = "id"
    )
    val gender: AppCatalogEntity?,

    @Relation(
        entity = ClientEntity::class,
        parentColumn = "client_id",
        entityColumn = "id"
    )
    val client: ClientEntity?
)
