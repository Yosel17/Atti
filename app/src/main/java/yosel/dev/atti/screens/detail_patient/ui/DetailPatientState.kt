package yosel.dev.atti.screens.detail_patient.ui

import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PatientWithDetailsModel

data class DetailPatientState(
    val isLoading: Boolean = true,
    val patientWithCatalogs: PatientWithDetailsModel = PatientWithDetailsModel(),
    val client: ClientModel = ClientModel(),
    val consultations: List<ConsultationWithDetailsModel> = emptyList(),
    val isLoadingConsultations: Boolean = false,
    val showBottomSheetDelete: Boolean = false,
    val deleteComment: String = "",
    val isLoadingDeletePatient: Boolean = false,
    val showDialogConfirmRestore: Boolean = false,
    val isLoadingRestorePatient: Boolean = false
)
