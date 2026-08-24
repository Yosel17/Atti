package yosel.dev.atti.core.room.tables.anamnesis

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity

data class AnamnesisWithDetailsEntity(
    @Embedded val anamnesis: AnamnesisEntity,
    @Relation(
        parentColumn = "food_brand_id",
        entityColumn = "id"
    )
    val foodBrand: AppCatalogEntity?,
    @Relation(
        parentColumn = "food_unit_type_id",
        entityColumn = "id"
    )
    val foodUnit: AppCatalogEntity?,
    @Relation(
        entity = AnamnesisEnvironmentOptionEntity::class,
        parentColumn = "id",
        entityColumn = "anamnesis_id"
    )
    val environmentOptions: List<AnamnesisEnviOptWithDetailsEntity> = emptyList(),
    @Relation(
        entity = AnamnesisVaccineEntity::class,
        parentColumn = "id",
        entityColumn = "anamnesis_id"
    )
    val vaccines: List<AnamnesisVaccineWithDetailsEntity> = emptyList(),
    @Relation(
        entity = AnamnesisDewormingEntity::class,
        parentColumn = "id",
        entityColumn = "anamnesis_id"
    )
    val dewormings: List<AnamnesisDewormingWithDetailsEntity> = emptyList()
)
