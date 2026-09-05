package yosel.dev.atti.screens.navigation_bar.directory.ui

import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientWithDetailsModel

data class DirectoryState(
    val isError: Boolean = false,
    val errorMessage: String = "",
    val isLoadingClients: Boolean = true,
    val clients: List<ClientModel> = emptyList(),
    val filteredClients: List<ClientModel> = emptyList(),
    val isLoadingPatients: Boolean = true,
    val isFirstPatients: Boolean = true,
    val patientsWithCatalogs: List<PatientWithDetailsModel> = emptyList(),
    val filteredPatientsWithCatalogs: List<PatientWithDetailsModel> = emptyList(),
    val selectedTabIndex: Int = 0,
    val clientSearchQuery: String = "",
    val patientSearchQuery: String = ""
)