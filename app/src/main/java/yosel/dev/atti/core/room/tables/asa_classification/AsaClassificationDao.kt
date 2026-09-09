package yosel.dev.atti.core.room.tables.asa_classification

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AsaClassificationDao {

    @Query("SELECT * FROM asa_classifications WHERE consultation_id = :consultationId AND status = 1")
    fun getAsaClassificationsByConsultationIdFlow(consultationId: String): Flow<List<AsaClassificationEntity>>

    @Query("SELECT * FROM asa_classifications WHERE consultation_id = :consultationId AND status = 1")
    suspend fun getAsaClassificationsByConsultationId(consultationId: String): List<AsaClassificationEntity>

    @Query("SELECT * FROM asa_classifications WHERE id = :id LIMIT 1")
    suspend fun getAsaClassificationById(id: String): AsaClassificationEntity?

    @Upsert
    suspend fun upsertAsaClassifications(classifications: List<AsaClassificationEntity>)

    @Upsert
    suspend fun upsertAsaClassification(classification: AsaClassificationEntity)

    @Query("DELETE FROM asa_classifications WHERE consultation_id = :consultationId")
    suspend fun deleteAsaClassificationsByConsultationId(consultationId: String)

    @Query("DELETE FROM asa_classifications WHERE id = :id")
    suspend fun deleteAsaClassificationById(id: String)

    @Query("UPDATE asa_classifications SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: Int)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM asa_classifications WHERE consultation_id = :consultationId AND status = 1")
    fun getAsaClassificationsWithDetailsByConsultationIdFlow(consultationId: String): Flow<List<AsaClassificationWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM asa_classifications WHERE consultation_id = :consultationId AND status = 1")
    suspend fun getAsaClassificationsWithDetailsByConsultationId(consultationId: String): List<AsaClassificationWithDetailsEntity>

    @Transaction
    @Query("SELECT * FROM asa_classifications WHERE id = :id LIMIT 1")
    suspend fun getAsaClassificationWithDetailsById(id: String): AsaClassificationWithDetailsEntity?

    // --- Sincronización en bloque de una consulta ---
    @Transaction
    suspend fun syncAsaClassificationsForConsultation(
        consultationId: String,
        classifications: List<AsaClassificationEntity>
    ) {
        deleteAsaClassificationsByConsultationId(consultationId)
        if (classifications.isNotEmpty()) {
            upsertAsaClassifications(classifications)
        }
    }
}