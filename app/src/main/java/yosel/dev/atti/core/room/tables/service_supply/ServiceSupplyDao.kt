package yosel.dev.atti.core.room.tables.service_supply

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceSupplyDao {

    @Query("SELECT * FROM service_supplies WHERE service_id = :serviceId ORDER BY id ASC")
    fun getSuppliesByServiceIdFlow(serviceId: String): Flow<List<ServiceSupplyEntity>>

    @Query("SELECT * FROM service_supplies WHERE service_id = :serviceId ORDER BY id ASC")
    suspend fun getSuppliesByServiceId(serviceId: String): List<ServiceSupplyEntity>

    @Query("SELECT * FROM service_supplies WHERE id = :supplyId")
    suspend fun getSupplyById(supplyId: Int): ServiceSupplyEntity?

    @Upsert
    suspend fun upsertSupplies(supplies: List<ServiceSupplyEntity>)

    @Upsert
    suspend fun upsertSupply(supply: ServiceSupplyEntity)

    @Query("UPDATE service_supplies SET status = :newStatus WHERE id = :supplyId")
    suspend fun updateSupplyStatus(supplyId: Int, newStatus: Int)

    @Query("DELETE FROM service_supplies WHERE service_id = :serviceId")
    suspend fun deleteSuppliesByServiceId(serviceId: String)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM service_supplies WHERE service_id = :serviceId ORDER BY id ASC")
    fun getSuppliesWithProductByServiceIdFlow(serviceId: String): Flow<List<ServiceSupplyWithDetailsEntity>>
}