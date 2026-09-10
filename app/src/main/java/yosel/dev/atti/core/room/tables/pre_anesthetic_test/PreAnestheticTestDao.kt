package yosel.dev.atti.core.room.tables.pre_anesthetic_test

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PreAnestheticTestDao {
    @Query("SELECT * FROM pre_anesthetic_tests WHERE consultation_id = :consultationId AND status = 1")
    fun getPreAnestheticTestsByConsultationIdFlow(consultationId: String): Flow<List<PreAnestheticTestEntity>>

    @Query("SELECT * FROM pre_anesthetic_tests WHERE consultation_id = :consultationId")
    suspend fun getPreAnestheticTestsByConsultationId(consultationId: String): List<PreAnestheticTestEntity>

    @Query("SELECT * FROM pre_anesthetic_tests WHERE id = :id LIMIT 1")
    suspend fun getPreAnestheticTestById(id: String): PreAnestheticTestEntity?

    @Upsert
    suspend fun upsertPreAnestheticTests(tests: List<PreAnestheticTestEntity>)

    @Upsert
    suspend fun upsertPreAnestheticTest(test: PreAnestheticTestEntity)

    @Query("DELETE FROM pre_anesthetic_tests WHERE consultation_id = :consultationId")
    suspend fun deletePreAnestheticTestsByConsultationId(consultationId: String)

    @Query("DELETE FROM pre_anesthetic_tests WHERE id = :id")
    suspend fun deletePreAnestheticTestById(id: String)

    @Query("UPDATE pre_anesthetic_tests SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: Int)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM pre_anesthetic_tests WHERE consultation_id = :consultationId AND status = 1")
    fun getPreAnestheticTestsWithDetailsByConsultationIdFlow(consultationId: String): Flow<List<PreAnestheticTestWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM pre_anesthetic_tests WHERE consultation_id = :consultationId AND status = 1")
    suspend fun getPreAnestheticTestsWithDetailsByConsultationId(consultationId: String): List<PreAnestheticTestWithDetailsEntity>

    @Transaction
    @Query("SELECT * FROM pre_anesthetic_tests WHERE id = :id LIMIT 1")
    suspend fun getPreAnestheticTestWithDetailsById(id: String): PreAnestheticTestWithDetailsEntity?

    // --- Sincronización en bloque de una consulta ---
    @Transaction
    suspend fun syncPreAnestheticTestsForConsultation(
        consultationId: String,
        tests: List<PreAnestheticTestEntity>
    ) {
        deletePreAnestheticTestsByConsultationId(consultationId)
        if (tests.isNotEmpty()) {
            upsertPreAnestheticTests(tests)
        }
    }
}