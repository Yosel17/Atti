package yosel.dev.atti.core.room.tables.observation

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservationDao {

    @Query("SELECT * FROM observations WHERE consultation_id = :consultationId AND status = 1 LIMIT 1")
    fun getObservationByConsultationIdFlow(consultationId: String): Flow<ObservationEntity?>

    @Query("SELECT * FROM observations WHERE consultation_id = :consultationId AND status = 1 LIMIT 1")
    suspend fun getObservationByConsultationId(consultationId: String): ObservationEntity?

    @Query("SELECT * FROM observations WHERE id = :id LIMIT 1")
    suspend fun getObservationById(id: String): ObservationEntity?

    @Upsert
    suspend fun upsertObservation(observation: ObservationEntity)

    @Query("DELETE FROM observations WHERE consultation_id = :consultationId")
    suspend fun deleteObservationByConsultationId(consultationId: String)

    @Query("DELETE FROM observations WHERE id = :id")
    suspend fun deleteObservationById(id: String)

    @Query("UPDATE observations SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: Int)
}