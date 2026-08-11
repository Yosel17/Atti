package yosel.dev.atti.core.room.tables.product

import androidx.room.Embedded
import androidx.room.Relation
import yosel.dev.atti.core.room.tables.app_catalog.AppCatalogEntity
import yosel.dev.atti.core.room.tables.supplier.SupplierEntity

data class ProductWithDetailsEntity(
    @Embedded val product: ProductEntity,
    @Relation(
        parentColumn = "supplier_id",
        entityColumn = "id"
    )
    val supplier: SupplierEntity?,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: AppCatalogEntity?,
    @Relation(
        parentColumn = "unit_type_id",
        entityColumn = "id"
    )
    val unitType: AppCatalogEntity?
)
