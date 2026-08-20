package yosel.dev.atti.screens.add_patient.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientModel

data class AddPatientState(
    val isEditMode: Boolean = false,
    val patientId: String? = null,
    val currentPatient: PatientModel? = null,
    val initialFormState: AddPatientFormState = AddPatientFormState(),
    val isLoadingDataInitial: Boolean = true,
    val isLoadingRegister: Boolean = false,
    val speciesCatalog: List<AppCatalogModel> = emptyList(),
    val genderCatalog: List<AppCatalogModel> = emptyList(),
    val clients: List<ClientModel> = emptyList(),
    val filteredClients: List<ClientModel> = emptyList(),
    val clientSearchQuery: String = "",
    val isClientSheetOpen: Boolean = false,
    val formState: AddPatientFormState = AddPatientFormState(),
    val isAddCatalogSheetOpen: Boolean = false,
    val activeCatalogTypeId: Int = 0,
    val activeCatalogTypeName: String = "",
    val isLoadingAddCatalog: Boolean = false,
    val isLoadingUpdatePatient: Boolean = false,
    val showCalendar: Boolean = false
)
