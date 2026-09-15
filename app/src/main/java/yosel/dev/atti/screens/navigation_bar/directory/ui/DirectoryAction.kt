package yosel.dev.atti.screens.navigation_bar.directory.ui

import yosel.dev.atti.core.models.filter.ClientFilter
import yosel.dev.atti.core.models.filter.PatientFilter

sealed interface DirectoryAction {
    data class OnTabSelected(val index: Int): DirectoryAction
    data class OnCallClick(val phoneNumber: String): DirectoryAction
    data class OnWhatsappClick(val phoneNumber: String): DirectoryAction
    data class OnClientSearchQueryChange(val query: String): DirectoryAction
    data class OnPatientSearchQueryChange(val query: String): DirectoryAction
    data class OnApplyClientFilter(val filter: ClientFilter): DirectoryAction
    data class OnApplyPatientFilter(val filter: PatientFilter): DirectoryAction
    // Control de visibilidad de sheets
    data class OnToggleClientFilterSheet(val isOpen: Boolean): DirectoryAction
    data class OnTogglePatientFilterSheet(val isOpen: Boolean): DirectoryAction
}