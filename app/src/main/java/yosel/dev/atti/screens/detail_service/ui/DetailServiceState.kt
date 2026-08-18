package yosel.dev.atti.screens.detail_service.ui

import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

data class DetailServiceState(
    val isLoading: Boolean = true,
    val serviceWithDetails: ServiceWithDetailsModel = ServiceWithDetailsModel(),
    val showDialogConfirmDelete: Boolean = false,
    val isLoadingDeleteService: Boolean = false,
    val showDialogConfirmRestore: Boolean = false,
    val isLoadingRestoreService: Boolean = false,
    val showDialogInformation: Boolean = false
)
