package yosel.dev.atti.screens.top_level.patients.ui

import yosel.dev.atti.core.models.filter.PatientFilter

sealed interface PatientsAction {
    data class OnSearchQueryChange(val query: String) : PatientsAction
    data class OnApplyFilter(val filter: PatientFilter) : PatientsAction
    data class OnToggleFilterSheet(val isOpen: Boolean) : PatientsAction
}