package yosel.dev.atti.core.room.tables.receipt

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM receipts ORDER BY id DESC")
    fun getAllReceiptsFlow(): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM receipts WHERE id = :id LIMIT 1")
    suspend fun getReceiptById(id: String): ReceiptEntity?

    @Query("SELECT * FROM receipts WHERE consultation_id = :consultationId LIMIT 1")
    suspend fun getReceiptByConsultationId(consultationId: String): ReceiptEntity?

    @Upsert
    suspend fun upsertReceipt(receipt: ReceiptEntity)

    @Upsert
    suspend fun upsertReceipts(receipts: List<ReceiptEntity>)

    @Upsert
    suspend fun upsertReceiptItems(items: List<ReceiptItemEntity>)

    @Query("DELETE FROM receipt_items WHERE receipt_id = :receiptId")
    suspend fun deleteItemsByReceiptId(receiptId: String)

    @Query("DELETE FROM receipts WHERE id = :id")
    suspend fun deleteReceiptById(id: String)

    @Query("UPDATE receipts SET status = :newStatus WHERE id = :id")
    suspend fun updateReceiptStatus(id: String, newStatus: Int)

    // --- Consultas con Relaciones ---
    @Transaction
    @Query("SELECT * FROM receipts ORDER BY id DESC")
    fun getAllReceiptsWithDetailsFlow(): Flow<List<ReceiptWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM receipts WHERE id = :id LIMIT 1")
    fun getReceiptWithDetailsByIdFlow(id: String): Flow<ReceiptWithDetailsEntity?>

    @Transaction
    @Query("SELECT * FROM receipts WHERE id = :id LIMIT 1")
    suspend fun getReceiptWithDetailsById(id: String): ReceiptWithDetailsEntity?

    @Transaction
    @Query("SELECT * FROM receipts WHERE consultation_id = :consultationId LIMIT 1")
    suspend fun getReceiptWithDetailsByConsultationId(consultationId: String): ReceiptWithDetailsEntity?

    // --- Guardado Atómico (Estilo Prescriptions) ---
    @Transaction
    suspend fun saveReceiptWithDetails(
        receipt: ReceiptEntity,
        items: List<ReceiptItemEntity>
    ) {
        upsertReceipt(receipt)
        deleteItemsByReceiptId(receipt.id)
        if (items.isNotEmpty()) {
            upsertReceiptItems(items)
        }
    }
}