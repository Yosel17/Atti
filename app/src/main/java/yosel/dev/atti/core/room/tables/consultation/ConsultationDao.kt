package yosel.dev.atti.core.room.tables.consultation

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultationDao {

    @Query("""
        SELECT * FROM consultations 
        ORDER BY 
            CASE WHEN status = 3 THEN 1 ELSE 0 END ASC,
            created_at DESC
    """)
    fun getAllConsultationsFlow(): Flow<List<ConsultationEntity>>

    @Query("SELECT * FROM consultations WHERE id = :consultationId")
    suspend fun getConsultationById(consultationId: String): ConsultationEntity?

    @Query("SELECT * FROM consultations WHERE patient_id = :patientId ORDER BY created_at DESC")
    fun getConsultationsByPatientIdFlow(patientId: String): Flow<List<ConsultationEntity>>

    @Upsert
    suspend fun upsertConsultations(consultations: List<ConsultationEntity>)

    @Upsert
    suspend fun upsertConsultation(consultation: ConsultationEntity)

    @Query("UPDATE consultations SET status = :newStatus WHERE id = :consultationId")
    suspend fun updateConsultationStatus(consultationId: String, newStatus: Int)

    // --- Consultas con Relaciones ---

    @Transaction
    @Query("""
        SELECT * FROM consultations 
        ORDER BY 
            CASE WHEN status = 3 THEN 1 ELSE 0 END ASC,
            created_at DESC
    """)
    fun getAllConsultationsWithDetailsFlow(): Flow<List<ConsultationWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM consultations WHERE id = :consultationId")
    fun getConsultationWithDetailsByIdFlow(consultationId: String): Flow<ConsultationWithDetailsEntity?>

    @Transaction
    @Query("SELECT * FROM consultations WHERE patient_id = :patientId ORDER BY created_at DESC")
    fun getConsultationsWithDetailsByPatientIdFlow(patientId: String): Flow<List<ConsultationWithDetailsEntity>>
}