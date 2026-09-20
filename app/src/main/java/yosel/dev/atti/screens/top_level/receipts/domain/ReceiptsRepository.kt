package yosel.dev.atti.screens.top_level.receipts.domain

import kotlinx.coroutines.flow.Flow
import yosel.dev.atti.core.models.model.ReceiptWithDetailsModel

interface ReceiptsRepository {
    fun getAllReceiptsWithDetails(): Flow<List<ReceiptWithDetailsModel>>
    suspend fun syncReceipts(): Result<Unit>
}