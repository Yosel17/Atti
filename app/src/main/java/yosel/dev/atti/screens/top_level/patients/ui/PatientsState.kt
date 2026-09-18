package yosel.dev.atti.screens.top_level.patients.ui

import yosel.dev.atti.core.models.filter.PatientFilter
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.PatientWithDetailsModel

data class PatientsState(
    val isLoading: Boolean = true,
    val patients: List<PatientWithDetailsModel> = emptyList(),
    val filteredPatients: List<PatientWithDetailsModel> = emptyList(),
    val searchQuery: String = "",
    val filter: PatientFilter = PatientFilter(),
    val showFilterSheet: Boolean = false,
    val availableSpecies: List<AppCatalogModel> = emptyList(),
    val availableGenders: List<AppCatalogModel> = emptyList()
)