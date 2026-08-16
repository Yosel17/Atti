package yosel.dev.atti.core.room.tables.service_supply

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.product.ProductEntity
import yosel.dev.atti.core.room.tables.product.ProductWithDetailsEntity

data class ServiceSupplyWithDetailsEntity(
    @Embedded val supply: ServiceSupplyEntity,
    @Relation(
        entity = ProductEntity::class,
        parentColumn = "product_id",
        entityColumn = "id"
    )
    val product: ProductWithDetailsEntity?
)
