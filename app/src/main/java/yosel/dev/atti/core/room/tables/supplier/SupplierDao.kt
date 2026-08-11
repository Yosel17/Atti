package yosel.dev.atti.core.room.tables.supplier

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierDao {
    @Query("""
        SELECT * FROM suppliers 
        ORDER BY 
            CASE WHEN status = 3 THEN 1 ELSE 0 END ASC,
            created_at DESC
    """)
    fun getAllSuppliersFlow(): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    suspend fun getAllSuppliers(): List<SupplierEntity>

    @Query("SELECT * FROM suppliers WHERE id = :supplierId")
    suspend fun getSupplierById(supplierId: String): SupplierEntity?

    @Query("SELECT * FROM suppliers WHERE id = :supplierId")
    fun getSupplierByIdFlow(supplierId: String): Flow<SupplierEntity?>

    @Upsert
    suspend fun upsertSuppliers(suppliers: List<SupplierEntity>)

    @Upsert
    suspend fun upsertSupplier(supplier: SupplierEntity)

    @Query("UPDATE suppliers SET status = :newStatus WHERE id = :supplierId")
    suspend fun updateSupplierStatus(supplierId: String, newStatus: Int)
}