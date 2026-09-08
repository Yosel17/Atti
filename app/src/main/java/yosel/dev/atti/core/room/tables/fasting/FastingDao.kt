package yosel.dev.atti.core.room.tables.fasting

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FastingDao {
    @Query("SELECT * FROM fasting WHERE consultation_id = :consultationId AND status = 1 LIMIT 1")
    fun getFastingByConsultationIdFlow(consultationId: String): Flow<FastingEntity?>

    @Query("SELECT * FROM fasting WHERE consultation_id = :consultationId AND status = 1 LIMIT 1")
    suspend fun getFastingByConsultationId(consultationId: String): FastingEntity?

    @Query("SELECT * FROM fasting WHERE id = :id LIMIT 1")
    suspend fun getFastingById(id: String): FastingEntity?

    @Upsert
    suspend fun upsertFasting(fasting: FastingEntity)

    @Query("DELETE FROM fasting WHERE consultation_id = :consultationId")
    suspend fun deleteFastingByConsultationId(consultationId: String)

    @Query("DELETE FROM fasting WHERE id = :id")
    suspend fun deleteFastingById(id: String)

    @Query("UPDATE fasting SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: Int)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM fasting WHERE consultation_id = :consultationId AND status = 1 LIMIT 1")
    fun getFastingWithDetailsByConsultationIdFlow(consultationId: String): Flow<FastingWithDetailsEntity?>

    @Transaction
    @Query("SELECT * FROM fasting WHERE consultation_id = :consultationId AND status = 1 LIMIT 1")
    suspend fun getFastingWithDetailsByConsultationId(consultationId: String): FastingWithDetailsEntity?

    @Transaction
    @Query("SELECT * FROM fasting WHERE id = :id LIMIT 1")
    suspend fun getFastingWithDetailsById(id: String): FastingWithDetailsEntity?
}