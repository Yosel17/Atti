package yosel.dev.atti.screens.detail_client.ui

import yosel.dev.atti.core.models.model.ClientWithPatientsModel

data class DetailClientState(
    val isLoading: Boolean = false,
    val clientWithPatients: ClientWithPatientsModel = ClientWithPatientsModel(),
)
