package yosel.dev.atti.screens.detail_patient.ui

import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel

data class DetailPatientState(
    val isLoading: Boolean = true,
    val patientWithCatalogs: PatientWithCatalogsModel = PatientWithCatalogsModel(),
    val client: ClientModel = ClientModel(),
    val showDialogConfirmDelete: Boolean = false,
    val isLoadingDeletePatient: Boolean = false,
    val showDialogConfirmRestore: Boolean = false,
    val isLoadingRestorePatient: Boolean = false
)
