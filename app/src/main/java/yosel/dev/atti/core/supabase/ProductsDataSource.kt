package yosel.dev.atti.core.supabase

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import yosel.dev.atti.core.models.dto.ProductDto
import yosel.dev.atti.core.utils.Constants
import javax.inject.Inject

class ProductsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {

    suspend fun getAllProductsWithDetails(): List<ProductDto>{
        return postgrest.from(Constants.PRODUCTS_SUPABASE)
            .select(
                columns = Columns.raw(
                    value = """
                *,
                supplier:suppliers!supplier_id(*),
                category:app_catalogs!category_id(*),
                unit_type:app_catalogs!unit_type_id(*)
                """.trimIndent()
                )
            ){
                order("status", Order.ASCENDING)
                order("created_at", Order.DESCENDING)
            }
            .decodeList<ProductDto>()
    }

    suspend fun insertAndGetProduct(product: ProductDto): ProductDto {
        return postgrest.from(Constants.PRODUCTS_SUPABASE)
            .insert(product) {
                select()
            }
            .decodeSingle<ProductDto>()
    }
}