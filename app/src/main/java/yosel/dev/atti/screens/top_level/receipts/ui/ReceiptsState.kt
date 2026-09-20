package yosel.dev.atti.screens.top_level.receipts.ui

import yosel.dev.atti.core.models.model.ReceiptWithDetailsModel

data class ReceiptsState(
    val isLoading: Boolean = true,
    val receipts: List<ReceiptWithDetailsModel> = emptyList(),
    val filteredReceipts: List<ReceiptWithDetailsModel> = emptyList(),
    val searchQuery: String = ""
)
