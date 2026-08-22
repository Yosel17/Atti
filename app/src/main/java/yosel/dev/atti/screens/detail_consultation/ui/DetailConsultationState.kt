package yosel.dev.atti.screens.detail_consultation.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

data class DetailConsultationState(
    val isLoading: Boolean = true,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    val consultationSteps: List<AppCatalogModel> = emptyList()
)
