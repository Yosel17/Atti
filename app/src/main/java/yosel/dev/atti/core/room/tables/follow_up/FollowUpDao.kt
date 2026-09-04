package yosel.dev.atti.core.room.tables.follow_up

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowUpDao {

    // --- Lecturas Simples ---
    @Query("SELECT * FROM follow_ups WHERE consultation_id = :consultationId AND status = 1 ORDER BY scheduled_at ASC")
    fun getFollowUpsByConsultationIdFlow(consultationId: String): Flow<List<FollowUpEntity>>

    @Query("SELECT * FROM follow_ups WHERE consultation_id = :consultationId AND status = 1 ORDER BY scheduled_at ASC")
    suspend fun getFollowUpsByConsultationId(consultationId: String): List<FollowUpEntity>

    @Query("SELECT * FROM follow_ups WHERE id = :id LIMIT 1")
    suspend fun getFollowUpById(id: String): FollowUpEntity?

    @Query("SELECT * FROM follow_ups WHERE patient_id = :patientId AND status = 1 ORDER BY scheduled_at ASC")
    fun getFollowUpsByPatientIdFlow(patientId: String): Flow<List<FollowUpEntity>>

    // --- Escrituras ---
    @Upsert
    suspend fun upsertFollowUp(followUp: FollowUpEntity)

    @Upsert
    suspend fun upsertFollowUps(followUps: List<FollowUpEntity>)

    @Query("DELETE FROM follow_ups WHERE id = :id")
    suspend fun deleteFollowUpById(id: String)

    @Query("DELETE FROM follow_ups WHERE consultation_id = :consultationId")
    suspend fun deleteFollowUpsByConsultationId(consultationId: String)

    @Query("UPDATE follow_ups SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: Int)

    // --- Consultas con Relaciones (PatientWithCatalogs + ConsultationWithDetails) ---
    @Transaction
    @Query("SELECT * FROM follow_ups WHERE consultation_id = :consultationId AND status = 1 ORDER BY scheduled_at ASC")
    fun getFollowUpsWithDetailsByConsultationIdFlow(consultationId: String): Flow<List<FollowUpWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM follow_ups WHERE consultation_id = :consultationId AND status = 1 LIMIT 1")
    suspend fun getFollowUpWithDetailsByConsultationId(consultationId: String): FollowUpWithDetailsEntity?

    @Transaction
    @Query("SELECT * FROM follow_ups WHERE id = :id LIMIT 1")
    suspend fun getFollowUpWithDetailsById(id: String): FollowUpWithDetailsEntity?

    @Transaction
    @Query("SELECT * FROM follow_ups WHERE patient_id = :patientId AND status = 1 ORDER BY scheduled_at ASC")
    fun getFollowUpsWithDetailsByPatientIdFlow(patientId: String): Flow<List<FollowUpWithDetailsEntity>>

    // --- Consultas Avanzadas por Rango de Fechas ---
    @Transaction
    @Query("""
        SELECT * FROM follow_ups 
        WHERE status = 1 
          AND scheduled_at >= :startOfDay 
          AND scheduled_at <= :endOfDay 
        ORDER BY scheduled_at ASC
    """)
    fun getFollowUpsByDateRangeFlow(startOfDay: String, endOfDay: String): Flow<List<FollowUpWithDetailsEntity>>

    @Transaction
    @Query("""
        SELECT * FROM follow_ups 
        WHERE status = 1 
          AND scheduled_at >= :nowIso 
        ORDER BY scheduled_at ASC
    """)
    fun getUpcomingFollowUpsFlow(nowIso: String): Flow<List<FollowUpWithDetailsEntity>>

    // Sincronización en bloque para una consulta
    @Transaction
    suspend fun syncFollowUpsForConsultation(
        consultationId: String,
        followUps: List<FollowUpEntity>
    ) {
        deleteFollowUpsByConsultationId(consultationId)
        if (followUps.isNotEmpty()) {
            upsertFollowUps(followUps)
        }
    }
}