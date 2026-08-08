package yosel.dev.atti.screens.add_patient.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClientModel

data class AddPatientState(
    val isLoadingDataInitial: Boolean = true,
    val isLoadingRegister: Boolean = false,
    val speciesCatalog: List<AppCatalogModel> = emptyList(),
    val genderCatalog: List<AppCatalogModel> = emptyList(),
    val clients: List<ClientModel> = emptyList(),
    val formState: AddPatientFormState = AddPatientFormState()
)
