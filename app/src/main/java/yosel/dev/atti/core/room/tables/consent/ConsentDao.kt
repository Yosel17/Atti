package yosel.dev.atti.core.room.tables.consent

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsentDao {
    @Query("SELECT * FROM consents WHERE consultation_id = :consultationId AND status = 1 LIMIT 1")
    fun getConsentByConsultationIdFlow(consultationId: String): Flow<ConsentEntity?>

    @Query("SELECT * FROM consents WHERE consultation_id = :consultationId LIMIT 1")
    suspend fun getConsentByConsultationId(consultationId: String): ConsentEntity?

    @Query("SELECT * FROM consents WHERE id = :id LIMIT 1")
    suspend fun getConsentById(id: String): ConsentEntity?

    @Upsert
    suspend fun upsertConsent(consent: ConsentEntity)

    @Query("DELETE FROM consents WHERE consultation_id = :consultationId")
    suspend fun deleteConsentByConsultationId(consultationId: String)

    @Query("DELETE FROM consents WHERE id = :id")
    suspend fun deleteConsentById(id: String)

    @Query("UPDATE consents SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: Int)
}