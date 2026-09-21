package yosel.dev.atti.screens.top_level.consultations.ui

sealed interface ConsultationsAction {
    data class OnSearchQueryChange(val query: String) : ConsultationsAction
}