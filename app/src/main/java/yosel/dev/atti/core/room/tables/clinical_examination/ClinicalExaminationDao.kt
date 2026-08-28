package yosel.dev.atti.core.room.tables.clinical_examination

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinicalExaminationDao {

    @Query("SELECT * FROM clinical_examinations WHERE consultation_id = :consultationId LIMIT 1")
    fun getClinicalExamByConsultationIdFlow(consultationId: String): Flow<ClinicalExaminationEntity?>

    @Query("SELECT * FROM clinical_examinations WHERE consultation_id = :consultationId LIMIT 1")
    suspend fun getClinicalExamByConsultationId(consultationId: String): ClinicalExaminationEntity?

    @Query("SELECT * FROM clinical_examinations WHERE id = :examId LIMIT 1")
    suspend fun getClinicalExamById(examId: String): ClinicalExaminationEntity?

    @Upsert
    suspend fun upsertClinicalExam(exam: ClinicalExaminationEntity)

    @Upsert
    suspend fun upsertLymphNodes(lymphNodes: List<ClinicalExamLymphNodeEntity>)

    @Query("DELETE FROM clinical_examination_lymph_nodes WHERE clinical_examination_id = :examId")
    suspend fun deleteLymphNodesByExamId(examId: String)

    @Transaction
    suspend fun saveClinicalExamWithDetails(
        exam: ClinicalExaminationEntity,
        lymphNodes: List<ClinicalExamLymphNodeEntity>
    ) {
        upsertClinicalExam(exam)
        if (lymphNodes.isNotEmpty()) {
            upsertLymphNodes(lymphNodes)
        }
    }

    @Transaction
    @Query("SELECT * FROM clinical_examinations WHERE consultation_id = :consultationId LIMIT 1")
    fun getClinicalExamWithDetailsByConsultationIdFlow(consultationId: String): Flow<ClinicalExamWithDetailsEntity?>

    @Transaction
    @Query("SELECT * FROM clinical_examinations WHERE consultation_id = :consultationId LIMIT 1")
    suspend fun getClinicalExamWithDetailsByConsultationId(consultationId: String): ClinicalExamWithDetailsEntity?

    @Transaction
    @Query("SELECT * FROM clinical_examinations WHERE id = :examId LIMIT 1")
    suspend fun getClinicalExamWithDetailsById(examId: String): ClinicalExamWithDetailsEntity?
}