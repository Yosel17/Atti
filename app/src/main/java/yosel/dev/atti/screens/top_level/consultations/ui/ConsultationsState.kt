package yosel.dev.atti.screens.top_level.consultations.ui

import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

data class ConsultationsState(
    val isLoading: Boolean = true,
    val consultations: List<ConsultationWithDetailsModel> = emptyList(),
    val filteredConsultations: List<ConsultationWithDetailsModel> = emptyList(),
    val searchQuery: String = ""
)
