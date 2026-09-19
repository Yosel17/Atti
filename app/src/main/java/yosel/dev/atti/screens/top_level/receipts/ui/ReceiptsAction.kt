package yosel.dev.atti.screens.top_level.receipts.ui

sealed interface ReceiptsAction {
    data class OnSearchQueryChange(val query: String) : ReceiptsAction
}