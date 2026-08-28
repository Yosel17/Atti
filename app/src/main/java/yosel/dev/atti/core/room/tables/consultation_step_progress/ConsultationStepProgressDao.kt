package yosel.dev.atti.core.room.tables.consultation_step_progress

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultationStepProgressDao {
    @Query("SELECT * FROM consultation_step_progress WHERE consultation_id = :consultationId")
    fun getProgressByConsultationIdFlow(consultationId: String): Flow<List<ConsultationStepProgressEntity>>

    @Upsert
    suspend fun upsertProgress(progressList: List<ConsultationStepProgressEntity>)

    @Upsert
    suspend fun upsertSingleProgress(progress: ConsultationStepProgressEntity)

    @Query("DELETE FROM consultation_step_progress WHERE consultation_id = :consultationId")
    suspend fun deleteProgressByConsultationId(consultationId: String)
}