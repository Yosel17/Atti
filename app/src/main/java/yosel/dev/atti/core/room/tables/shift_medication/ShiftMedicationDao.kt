package yosel.dev.atti.core.room.tables.shift_medication

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftMedicationDao {
    @Query("SELECT * FROM shift_medications WHERE consultation_id = :consultationId AND status = 1")
    fun getShiftMedicationsByConsultationIdFlow(consultationId: String): Flow<List<ShiftMedicationEntity>>

    @Query("SELECT * FROM shift_medications WHERE consultation_id = :consultationId")
    suspend fun getShiftMedicationsByConsultationId(consultationId: String): List<ShiftMedicationEntity>

    @Query("SELECT * FROM shift_medications WHERE id = :id LIMIT 1")
    suspend fun getShiftMedicationById(id: String): ShiftMedicationEntity?

    @Upsert
    suspend fun upsertShiftMedications(medications: List<ShiftMedicationEntity>)

    @Upsert
    suspend fun upsertShiftMedication(medication: ShiftMedicationEntity)

    @Query("DELETE FROM shift_medications WHERE consultation_id = :consultationId")
    suspend fun deleteShiftMedicationsByConsultationId(consultationId: String)

    @Query("DELETE FROM shift_medications WHERE id = :id")
    suspend fun deleteShiftMedicationById(id: String)

    @Query("UPDATE shift_medications SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: Int)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM shift_medications WHERE consultation_id = :consultationId AND status = 1")
    fun getShiftMedicationsWithDetailsByConsultationIdFlow(consultationId: String): Flow<List<ShiftMedicationWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM shift_medications WHERE consultation_id = :consultationId AND status = 1")
    suspend fun getShiftMedicationsWithDetailsByConsultationId(consultationId: String): List<ShiftMedicationWithDetailsEntity>

    @Transaction
    @Query("SELECT * FROM shift_medications WHERE id = :id LIMIT 1")
    suspend fun getShiftMedicationWithDetailsById(id: String): ShiftMedicationWithDetailsEntity?

    // --- Sincronización en bloque para una consulta ---
    @Transaction
    suspend fun syncShiftMedicationsForConsultation(
        consultationId: String,
        medications: List<ShiftMedicationEntity>
    ) {
        deleteShiftMedicationsByConsultationId(consultationId)
        if (medications.isNotEmpty()) {
            upsertShiftMedications(medications)
        }
    }
}