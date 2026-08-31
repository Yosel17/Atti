package yosel.dev.atti.screens.diagnosis_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.DiagnosisWithDetailsModel

data class DiagnosisFormState(
    val isEditMode: Boolean = false,
    val diagnosisId: String? = null,
    val initialFormInputState: DiagnosisFormInputsState = DiagnosisFormInputsState(),
    val formInputState: DiagnosisFormInputsState = DiagnosisFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetCatalogs: Boolean = false,
    val isLoadingSaveDiagnosis: Boolean = false,
    val isLoadingUpdateDiagnosis: Boolean = false,
    val isLoadingAddTag: Boolean = false,
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    val existingDiagnosesWithDetails: List<DiagnosisWithDetailsModel> = emptyList(),
    // Catálogos Diagnóstico (Tipo 18)
    val diagnosisCatalogs: List<AppCatalogModel> = emptyList(),
    val filteredDiagnosisCatalogs: List<AppCatalogModel> = emptyList(),
    val searchQuery: String = ""
)
