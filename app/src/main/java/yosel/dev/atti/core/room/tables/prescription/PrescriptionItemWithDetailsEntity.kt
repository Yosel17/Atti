package yosel.dev.atti.core.room.tables.prescription

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.product.ProductEntity
import yosel.dev.atti.core.room.tables.product.ProductWithDetailsEntity

data class PrescriptionItemWithDetailsEntity(
    @Embedded val item: PrescriptionItemEntity,
    @Relation(
        entity = ProductEntity::class,
        parentColumn = "product_id",
        entityColumn = "id"
    )
    val product: ProductWithDetailsEntity? = null
)
