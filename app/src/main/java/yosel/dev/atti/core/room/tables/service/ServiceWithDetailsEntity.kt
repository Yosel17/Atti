package yosel.dev.atti.core.room.tables.service

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyEntity
import yosel.dev.atti.core.room.tables.service_supply.ServiceSupplyWithDetailsEntity

data class ServiceWithDetailsEntity(
    @Embedded val service: ServiceEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: AppCatalogEntity?,
    @Relation(
        entity = ServiceSupplyEntity::class,
        parentColumn = "id",
        entityColumn = "service_id"
    )
    val supplies: List<ServiceSupplyWithDetailsEntity> = emptyList()
)
