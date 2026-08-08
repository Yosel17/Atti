package yosel.dev.atti.screens.add_patient.ui

import yosel.dev.atti.core.models.model.ClientModel

sealed interface AddPatientAction {

    data object RegisterPatient : AddPatientAction

    data object TryCatalogsAgain: AddPatientAction

    data class OnChangeValueFormState(val value: String, val field: Int) : AddPatientAction

    data class OnSelectSpecies(val id: Int) : AddPatientAction

    data class OnSelectGender(val id: Int) : AddPatientAction

    data class OnSelectClient(val client: ClientModel) : AddPatientAction

    data class OnToggleNeutered(val value: Boolean) : AddPatientAction
}
