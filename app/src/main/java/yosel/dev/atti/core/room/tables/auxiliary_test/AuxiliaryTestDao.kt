package yosel.dev.atti.core.room.tables.auxiliary_test

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AuxiliaryTestDao {

    @Query("SELECT * FROM auxiliary_tests WHERE consultation_id = :consultationId AND status = 1")
    fun getAuxiliaryTestsByConsultationIdFlow(consultationId: String): Flow<List<AuxiliaryTestEntity>>

    @Query("SELECT * FROM auxiliary_tests WHERE consultation_id = :consultationId AND status = 1")
    suspend fun getAuxiliaryTestsByConsultationId(consultationId: String): List<AuxiliaryTestEntity>

    @Query("SELECT * FROM auxiliary_tests WHERE id = :id LIMIT 1")
    suspend fun getAuxiliaryTestById(id: String): AuxiliaryTestEntity?

    @Upsert
    suspend fun upsertAuxiliaryTests(tests: List<AuxiliaryTestEntity>)

    @Upsert
    suspend fun upsertAuxiliaryTest(test: AuxiliaryTestEntity)

    @Query("DELETE FROM auxiliary_tests WHERE consultation_id = :consultationId")
    suspend fun deleteAuxiliaryTestsByConsultationId(consultationId: String)

    @Query("DELETE FROM auxiliary_tests WHERE id = :id")
    suspend fun deleteAuxiliaryTestById(id: String)

    @Query("UPDATE auxiliary_tests SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: Int)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM auxiliary_tests WHERE consultation_id = :consultationId AND status = 1")
    fun getAuxiliaryTestsWithDetailsByConsultationIdFlow(consultationId: String): Flow<List<AuxiliaryTestWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM auxiliary_tests WHERE consultation_id = :consultationId AND status = 1")
    suspend fun getAuxiliaryTestsWithDetailsByConsultationId(consultationId: String): List<AuxiliaryTestWithDetailsEntity>

    @Transaction
    @Query("SELECT * FROM auxiliary_tests WHERE id = :id LIMIT 1")
    suspend fun getAuxiliaryTestWithDetailsById(id: String): AuxiliaryTestWithDetailsEntity?

    // --- Sincronización en bloque de una consulta ---
    @Transaction
    suspend fun syncAuxiliaryTestsForConsultation(
        consultationId: String,
        tests: List<AuxiliaryTestEntity>
    ) {
        deleteAuxiliaryTestsByConsultationId(consultationId)
        if (tests.isNotEmpty()) {
            upsertAuxiliaryTests(tests)
        }
    }
}