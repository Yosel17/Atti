package yosel.dev.atti.core.room.tables.pre_anesthetic_test

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.product.ProductEntity
import yosel.dev.atti.core.room.tables.product.ProductWithDetailsEntity
import yosel.dev.atti.core.room.tables.service.ServiceEntity
import yosel.dev.atti.core.room.tables.service.ServiceWithDetailsEntity

data class PreAnestheticTestWithDetailsEntity(
    @Embedded val preAnestheticTest: PreAnestheticTestEntity,
    @Relation(
        entity = ProductEntity::class,
        parentColumn = "product_id",
        entityColumn = "id"
    )
    val product: ProductWithDetailsEntity?,
    @Relation(
        entity = ServiceEntity::class,
        parentColumn = "service_id",
        entityColumn = "id"
    )
    val service: ServiceWithDetailsEntity?
)
