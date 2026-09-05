package yosel.dev.atti.core.room.tables.treatment

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TreatmentDao {

    @Query("SELECT * FROM treatments WHERE consultation_id = :consultationId AND status = 1")
    fun getTreatmentsByConsultationIdFlow(consultationId: String): Flow<List<TreatmentEntity>>

    @Query("SELECT * FROM treatments WHERE consultation_id = :consultationId")
    suspend fun getTreatmentsByConsultationId(consultationId: String): List<TreatmentEntity>

    @Query("SELECT * FROM treatments WHERE id = :id LIMIT 1")
    suspend fun getTreatmentById(id: String): TreatmentEntity?

    @Upsert
    suspend fun upsertTreatments(treatments: List<TreatmentEntity>)

    @Upsert
    suspend fun upsertTreatment(treatment: TreatmentEntity)

    @Query("DELETE FROM treatments WHERE consultation_id = :consultationId")
    suspend fun deleteTreatmentsByConsultationId(consultationId: String)

    @Query("DELETE FROM treatments WHERE id = :id")
    suspend fun deleteTreatmentById(id: String)

    @Query("UPDATE treatments SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: Int)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM treatments WHERE consultation_id = :consultationId AND status = 1")
    fun getTreatmentsWithDetailsByConsultationIdFlow(consultationId: String): Flow<List<TreatmentWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM treatments WHERE consultation_id = :consultationId AND status = 1")
    suspend fun getTreatmentsWithDetailsByConsultationId(consultationId: String): List<TreatmentWithDetailsEntity>

    @Transaction
    @Query("SELECT * FROM treatments WHERE id = :id LIMIT 1")
    suspend fun getTreatmentWithDetailsById(id: String): TreatmentWithDetailsEntity?

    // --- Sincronización en bloque de una consulta ---
    @Transaction
    suspend fun syncTreatmentsForConsultation(
        consultationId: String,
        treatments: List<TreatmentEntity>
    ) {
        deleteTreatmentsByConsultationId(consultationId)
        if (treatments.isNotEmpty()) {
            upsertTreatments(treatments)
        }
    }
}