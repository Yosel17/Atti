package yosel.dev.atti.screens.detail_client.ui

import yosel.dev.atti.core.models.model.ClientWithPatientsWithCatalogsModel

data class DetailClientState(
    val isLoading: Boolean = true,
    val clientWithPatientsWithCatalogs: ClientWithPatientsWithCatalogsModel = ClientWithPatientsWithCatalogsModel(),
    val isEditing: Boolean = false,
    val isLoadingUpdate: Boolean = false,
    val editFormState: EditClientFormState = EditClientFormState(),
    val initialEditFormState: EditClientFormState = EditClientFormState(),
    val showDialogConfirmDelete: Boolean = false,
    val isLoadingDeleteClient: Boolean = false,
    val showDialogConfirmRestore: Boolean = false,
    val isLoadingRestoreClient: Boolean = false,
    val showDialogInformation: Boolean = false
)
