package yosel.dev.atti.screens.navigation_bar.consultation.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel

data class ConsultationState(
    val isLoadingData: Boolean = true,
    val isStartingConsultation: Boolean = false,
    val activeConsultation: ConsultationWithDetailsModel? = null,
    val patients: List<PatientWithCatalogsModel> = emptyList(),
    val filteredPatients: List<PatientWithCatalogsModel> = emptyList(),
    val patientSearchQuery: String = "",
    val selectedPatient: PatientWithCatalogsModel? = null,
    val consultationReasons: List<AppCatalogModel> = emptyList(),
    val selectedReason: AppCatalogModel? = null,
    val showConfirmDialog: Boolean = false,
    val pendingSelectedReason: AppCatalogModel? = null
) {
    val hasActiveConsultation: Boolean
        get() = activeConsultation != null
}
