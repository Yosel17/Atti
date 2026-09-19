package yosel.dev.atti.screens.top_level.receipts.ui

sealed interface ReceiptsEvent {
    data class ShowSnackBarError(val message: String) : ReceiptsEvent
}