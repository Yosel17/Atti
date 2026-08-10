package yosel.dev.atti.screens.detail_patient.ui

import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientModel

data class DetailPatientState(
    val isLoading: Boolean = true,
    val patient: PatientModel = PatientModel(),
    val client: ClientModel = ClientModel(),
    val showDialogConfirmDelete: Boolean = false,
    val isLoadingDeletePatient: Boolean = false
)
