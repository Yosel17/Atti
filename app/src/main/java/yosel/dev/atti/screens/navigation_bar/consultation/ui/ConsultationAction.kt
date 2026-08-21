package yosel.dev.atti.screens.navigation_bar.consultation.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel

sealed interface ConsultationAction {
    data class OnSearchPatientQueryChange(val query: String) : ConsultationAction
    data class OnSelectPatient(val patient: PatientWithCatalogsModel) : ConsultationAction
    data class OnSelectConsultationReason(val reason: AppCatalogModel) : ConsultationAction
    data object OnConfirmStartConsultation : ConsultationAction
    data object OnDismissConfirmDialog : ConsultationAction
    data object OnRetryInitialData : ConsultationAction
}