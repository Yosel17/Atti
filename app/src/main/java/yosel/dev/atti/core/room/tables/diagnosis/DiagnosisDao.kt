package yosel.dev.atti.core.room.tables.diagnosis

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosisDao {
    @Query("SELECT * FROM diagnoses WHERE consultation_id = :consultationId AND status = 1")
    fun getDiagnosesByConsultationIdFlow(consultationId: String): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnoses WHERE consultation_id = :consultationId AND status = 1")
    suspend fun getDiagnosesByConsultationId(consultationId: String): List<DiagnosisEntity>

    @Query("SELECT * FROM diagnoses WHERE id = :id LIMIT 1")
    suspend fun getDiagnosisById(id: String): DiagnosisEntity?

    @Upsert
    suspend fun upsertDiagnoses(diagnoses: List<DiagnosisEntity>)

    @Upsert
    suspend fun upsertDiagnosis(diagnosis: DiagnosisEntity)

    @Query("DELETE FROM diagnoses WHERE consultation_id = :consultationId")
    suspend fun deleteDiagnosesByConsultationId(consultationId: String)

    @Query("DELETE FROM diagnoses WHERE id = :id")
    suspend fun deleteDiagnosisById(id: String)

    @Query("UPDATE diagnoses SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: Int)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM diagnoses WHERE consultation_id = :consultationId AND status = 1")
    fun getDiagnosesWithDetailsByConsultationIdFlow(consultationId: String): Flow<List<DiagnosisWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM diagnoses WHERE consultation_id = :consultationId AND status = 1")
    suspend fun getDiagnosesWithDetailsByConsultationId(consultationId: String): List<DiagnosisWithDetailsEntity>

    @Transaction
    @Query("SELECT * FROM diagnoses WHERE id = :id LIMIT 1")
    suspend fun getDiagnosisWithDetailsById(id: String): DiagnosisWithDetailsEntity?

    // --- Sincronización en bloque de una consulta ---
    @Transaction
    suspend fun syncDiagnosesForConsultation(
        consultationId: String,
        diagnoses: List<DiagnosisEntity>
    ) {
        deleteDiagnosesByConsultationId(consultationId)
        if (diagnoses.isNotEmpty()) {
            upsertDiagnoses(diagnoses)
        }
    }
}