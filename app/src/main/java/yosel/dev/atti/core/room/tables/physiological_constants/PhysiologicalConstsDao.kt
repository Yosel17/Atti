package yosel.dev.atti.core.room.tables.physiological_constants

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PhysiologicalConstsDao {

    @Query("SELECT * FROM physiological_consts WHERE consultation_id = :consultationId LIMIT 1")
    fun getConstantsByConsultationIdFlow(consultationId: String): Flow<PhysiologicalConstsEntity?>

    @Query("SELECT * FROM physiological_consts WHERE consultation_id = :consultationId LIMIT 1")
    suspend fun getConstantsByConsultationId(consultationId: String): PhysiologicalConstsEntity?

    @Query("SELECT * FROM physiological_consts WHERE id = :id LIMIT 1")
    suspend fun getConstantsById(id: String): PhysiologicalConstsEntity?

    @Upsert
    suspend fun upsertConstants(constants: PhysiologicalConstsEntity)

    @Query("UPDATE physiological_consts SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: Int)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM physiological_consts WHERE consultation_id = :consultationId LIMIT 1")
    fun getConstantsWithDetailsByConsultationIdFlow(consultationId: String): Flow<PhysiologicalConstantsWithDetailsEntity?>

    @Transaction
    @Query("SELECT * FROM physiological_consts WHERE consultation_id = :consultationId LIMIT 1")
    suspend fun getConstantsWithDetailsByConsultationId(consultationId: String): PhysiologicalConstantsWithDetailsEntity?

    @Transaction
    @Query("SELECT * FROM physiological_consts WHERE id = :id LIMIT 1")
    suspend fun getConstantsWithDetailsById(id: String): PhysiologicalConstantsWithDetailsEntity?
}