package yosel.dev.atti.screens.product_form.domain

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductModel
import yosel.dev.atti.core.models.model.SupplierModel

interface ProductFormRepository {

    suspend fun getAppCatalogsByTypes(types: List<Int>): Result<List<AppCatalogModel>>

    suspend fun insertCatalog(catalog: AppCatalogModel): Result<AppCatalogModel>

    suspend fun insertProduct(product: ProductModel): Result<Unit>

    suspend fun getSuppliers(): Result<List<SupplierModel>>

    suspend fun updateProduct(product: ProductModel): Result<Unit>

    suspend fun getProductByIdRoom(productId: String): Result<ProductModel>
}