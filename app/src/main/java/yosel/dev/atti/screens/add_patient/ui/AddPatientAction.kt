package yosel.dev.atti.screens.add_patient.ui

import yosel.dev.atti.core.models.model.ClientModel

sealed interface AddPatientAction {
    data object RegisterPatient : AddPatientAction
    data object TryCatalogsAgain : AddPatientAction
    data class OnChangeValueFormState(val value: String, val field: Int) : AddPatientAction
    data class OnSelectSpecies(val id: Int) : AddPatientAction
    data class OnSelectGender(val id: Int) : AddPatientAction
    data class OnToggleNeutered(val value: Boolean) : AddPatientAction

    // Nueva acción para calcular la edad desde el calendario
    data class OnCalculateAgeFromBirthDate(val birthDateMillis: Long) : AddPatientAction
    data class ToggleShowCalendar(val show: Boolean) : AddPatientAction

    // Acciones del BottomSheet de Selección de Cliente
    data object OnOpenClientSheet : AddPatientAction
    data object OnDismissClientSheet : AddPatientAction
    data class OnSearchClientQueryChange(val query: String) : AddPatientAction
    data class OnSelectClient(val client: ClientModel) : AddPatientAction

    // Acciones del Catálogo
    data class OnOpenAddCatalogSheet(val catalogTypeId: Int, val catalogTypeName: String) : AddPatientAction
    data object OnDismissAddCatalogSheet : AddPatientAction
    data class OnSaveCatalog(val name: String) : AddPatientAction
}
