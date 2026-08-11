package yosel.dev.atti.core.room.tables.client

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Query("""
        SELECT * FROM clients 
        ORDER BY 
        CASE WHEN status = 3 THEN 1 ELSE 0 END ASC,
        created_at DESC
""")
    fun getAllClientsFlow(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients ORDER BY created_at DESC")
    suspend fun getAllClients(): List<ClientEntity>

    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientById(clientId: String): ClientEntity?

    @Upsert
    suspend fun upsertClients(clients: List<ClientEntity>)

    @Upsert
    suspend fun upsertClient(client: ClientEntity)

    @Query("DELETE FROM clients WHERE id = :clientId")
    suspend fun deleteClientById(clientId: String)

    @Query("DELETE FROM clients")
    suspend fun clearAllClients()

    @Query("UPDATE clients SET status = :newStatus WHERE id = :clientId")
    suspend fun updateClientStatus(clientId: String, newStatus: Int)

    @Transaction
    @Query("SELECT * FROM clients WHERE id = :clientId")
    fun getClientWithPatientsFlow(clientId: String): Flow<ClientWithPatientsEntity?>

    @Transaction
    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientWithPatients(clientId: String): ClientWithPatientsEntity?

    @Transaction
    suspend fun clearAndInsertClients(clients: List<ClientEntity>) {
        clearAllClients()
        upsertClients(clients)
    }
}