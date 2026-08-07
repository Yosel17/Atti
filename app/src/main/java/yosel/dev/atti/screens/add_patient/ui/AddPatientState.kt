package yosel.dev.atti.screens.add_patient.ui

import yosel.dev.atti.core.models.model.AppCatalogModel

data class AddPatientState(
    val isLoadingCatalogs: Boolean = true,
    val speciesCatalog: List<AppCatalogModel> = emptyList(),
    val genderCatalog: List<AppCatalogModel> = emptyList(),
)
