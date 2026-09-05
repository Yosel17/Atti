package yosel.dev.atti.core.room.tables.prescription

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PrescriptionDao {

    @Query("SELECT * FROM prescriptions WHERE consultation_id = :consultationId LIMIT 1")
    fun getPrescriptionByConsultationIdFlow(consultationId: String): Flow<PrescriptionEntity?>

    @Query("SELECT * FROM prescriptions WHERE consultation_id = :consultationId LIMIT 1")
    suspend fun getPrescriptionByConsultationId(consultationId: String): PrescriptionEntity?

    @Query("SELECT * FROM prescriptions WHERE id = :id LIMIT 1")
    suspend fun getPrescriptionById(id: String): PrescriptionEntity?

    @Upsert
    suspend fun upsertPrescription(prescription: PrescriptionEntity)

    @Upsert
    suspend fun upsertPrescriptionItems(items: List<PrescriptionItemEntity>)

    @Query("DELETE FROM prescription_items WHERE prescription_id = :prescriptionId")
    suspend fun deleteItemsByPrescriptionId(prescriptionId: String)

    @Query("DELETE FROM prescriptions WHERE id = :id")
    suspend fun deletePrescriptionById(id: String)

    @Query("DELETE FROM prescriptions WHERE consultation_id = :consultationId")
    suspend fun deletePrescriptionByConsultationId(consultationId: String)

    @Query("""
    SELECT pi.* FROM prescription_items pi
    INNER JOIN prescriptions p ON pi.prescription_id = p.id
    WHERE p.consultation_id = :consultationId
    """)
    suspend fun getPrescriptionItemsByConsultationId(consultationId: String): List<PrescriptionItemEntity>

    // --- Transacción atómica completa estilo Anamnesis ---
    @Transaction
    suspend fun savePrescriptionWithDetails(
        prescription: PrescriptionEntity,
        items: List<PrescriptionItemEntity>
    ) {
        upsertPrescription(prescription)
        deleteItemsByPrescriptionId(prescription.id)
        if (items.isNotEmpty()) {
            upsertPrescriptionItems(items)
        }
    }

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM prescriptions WHERE consultation_id = :consultationId AND status = 1 LIMIT 1")
    fun getPrescriptionWithDetailsByConsultationIdFlow(consultationId: String): Flow<PrescriptionWithDetailsEntity?>

    @Transaction
    @Query("SELECT * FROM prescriptions WHERE consultation_id = :consultationId AND status = 1 LIMIT 1")
    suspend fun getPrescriptionWithDetailsByConsultationId(consultationId: String): PrescriptionWithDetailsEntity?

    @Transaction
    @Query("SELECT * FROM prescriptions WHERE id = :id LIMIT 1")
    suspend fun getPrescriptionWithDetailsById(id: String): PrescriptionWithDetailsEntity?
}