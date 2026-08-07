package yosel.dev.atti.screens.detail_client.ui

import yosel.dev.atti.core.models.model.ClientWithPatientsModel

data class DetailClientState(
    val isLoading: Boolean = true,
    val clientWithPatients: ClientWithPatientsModel = ClientWithPatientsModel(),
    val isEditing: Boolean = false,
    val isLoadingUpdate: Boolean = false,
    val editFormState: EditClientFormState = EditClientFormState()
)
