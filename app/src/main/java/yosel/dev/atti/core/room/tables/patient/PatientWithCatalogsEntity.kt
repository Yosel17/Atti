package yosel.dev.atti.core.room.tables.patient

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class PatientWithCatalogsEntity(
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
    val gender: AppCatalogEntity?
)
