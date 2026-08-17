package yosel.dev.atti.core.room.tables.product

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("""
        SELECT * FROM products 
        ORDER BY 
            CASE WHEN status = 3 THEN 1 ELSE 0 END ASC,
            created_at DESC
    """)
    fun getAllProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE supplier_id = :supplierId ORDER BY commercial_name ASC")
    fun getProductsBySupplierIdFlow(supplierId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?

    @Upsert
    suspend fun upsertProducts(products: List<ProductEntity>)

    @Upsert
    suspend fun upsertProduct(product: ProductEntity)


    @Query("UPDATE products SET status = :newStatus WHERE id = :productId")
    suspend fun updateProductStatus(productId: String, newStatus: Int)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("""
        SELECT * FROM products 
        ORDER BY 
            CASE WHEN status = 3 THEN 1 ELSE 0 END ASC,
            created_at DESC
    """)
    fun getAllProductsWithDetailsFlow(): Flow<List<ProductWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductWithDetailsByIdFlow(productId: String): Flow<ProductWithDetailsEntity?>

    @Transaction
    @Query("SELECT * FROM products WHERE status = 1")
    suspend fun getActiveProductsWithDetails(): List<ProductWithDetailsEntity>
}