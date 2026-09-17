package yosel.dev.atti.screens.top_level.directory.ui

import yosel.dev.atti.core.models.filter.ClientFilter
import yosel.dev.atti.core.models.filter.PatientFilter
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientWithDetailsModel

data class DirectoryState(
    val isError: Boolean = false,
    val errorMessage: String = "",
    // Clientes
    val isLoadingClients: Boolean = true,
    val clients: List<ClientModel> = emptyList(),
    val filteredClients: List<ClientModel> = emptyList(),
    val clientSearchQuery: String = "",
    val clientFilter: ClientFilter = ClientFilter(),
    val showClientFilterSheet: Boolean = false,
    // Pacientes
    val isLoadingPatients: Boolean = true,
    val isFirstPatients: Boolean = true,
    val patientsWithCatalogs: List<PatientWithDetailsModel> = emptyList(),
    val filteredPatientsWithCatalogs: List<PatientWithDetailsModel> = emptyList(),
    val patientSearchQuery: String = "",
    val patientFilter: PatientFilter = PatientFilter(),
    val showPatientFilterSheet: Boolean = false,
    val availableSpecies: List<AppCatalogModel> = emptyList(),
    val availableGenders: List<AppCatalogModel> = emptyList(),
    val selectedTabIndex: Int = 0
)