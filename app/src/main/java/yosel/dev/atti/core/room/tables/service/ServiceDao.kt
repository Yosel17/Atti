package yosel.dev.atti.core.room.tables.service

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("""
        SELECT * FROM services 
        ORDER BY 
            CASE WHEN status = 3 THEN 1 ELSE 0 END ASC,
            created_at DESC
    """)
    fun getAllServicesFlow(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :serviceId")
    suspend fun getServiceById(serviceId: String): ServiceEntity?

    @Upsert
    suspend fun upsertServices(services: List<ServiceEntity>)

    @Upsert
    suspend fun upsertService(service: ServiceEntity)

    @Query("UPDATE services SET status = :newStatus WHERE id = :serviceId")
    suspend fun updateServiceStatus(serviceId: String, newStatus: Int)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("""
        SELECT * FROM services 
        ORDER BY 
            CASE WHEN status = 3 THEN 1 ELSE 0 END ASC,
            created_at DESC
    """)
    fun getAllServicesWithCatalogFlow(): Flow<List<ServiceWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM services WHERE id = :serviceId")
    fun getServiceWithCatalogByIdFlow(serviceId: String): Flow<ServiceWithDetailsEntity?>

    @Transaction
    @Query("SELECT * FROM services WHERE id = :serviceId")
    suspend fun getServiceWithDetailsById(serviceId: String): ServiceWithDetailsEntity?

    @Transaction
    @Query("SELECT * FROM services WHERE status = 1")
    suspend fun getActiveServicesWithDetails(): List<ServiceWithDetailsEntity>
}