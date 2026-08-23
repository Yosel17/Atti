package yosel.dev.atti.core.room.tables.consultation_type_step

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultationTypeStepDao {
    @Query("SELECT * FROM consultation_type_steps WHERE consultation_type_id = :consultationTypeId ORDER BY step_order ASC")
    fun getStepsByConsultationTypeIdFlow(consultationTypeId: Int): Flow<List<ConsultationTypeStepEntity>>

    @Query("SELECT * FROM consultation_type_steps WHERE consultation_type_id = :consultationTypeId ORDER BY step_order ASC")
    suspend fun getStepsByConsultationTypeId(consultationTypeId: Int): List<ConsultationTypeStepEntity>

    @Query("SELECT * FROM consultation_type_steps WHERE id = :id")
    suspend fun getStepById(id: Int): ConsultationTypeStepEntity?

    @Upsert
    suspend fun upsertSteps(steps: List<ConsultationTypeStepEntity>)

    @Upsert
    suspend fun upsertStep(step: ConsultationTypeStepEntity)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM consultation_type_steps WHERE consultation_type_id = :consultationTypeId ORDER BY step_order ASC")
    fun getStepsWithDetailsByConsultationTypeIdFlow(consultationTypeId: Int): Flow<List<ConsultationTypeStepWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM consultation_type_steps WHERE id = :id")
    suspend fun getStepWithDetailsById(id: Int): ConsultationTypeStepWithDetailsEntity?
}