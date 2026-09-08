package yosel.dev.atti.core.room.tables.auxiliary_test

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class AuxiliaryTestWithDetailsEntity(
    @Embedded val auxiliaryTest: AuxiliaryTestEntity,
    @Relation(
        parentColumn = "test_catalog_id",
        entityColumn = "id"
    )
    val catalog: AppCatalogEntity?
)