package yosel.dev.atti.screens.follow_up_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.FollowUpWithDetailsModel

data class FollowUpFormState(
    val isEditMode: Boolean = false,
    val followUpId: String? = null,
    val formInputState: FollowUpFormInputsState = FollowUpFormInputsState(),
    val initialFormInputState: FollowUpFormInputsState = FollowUpFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetData: Boolean = false,
    val isLoadingSaveFollowUp: Boolean = false,
    val isLoadingUpdateFollowUp: Boolean = false,
    val showDialogConfirm: Boolean = false,
    val showDatePickerDialog: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    val existingFollowUpWithDetails: FollowUpWithDetailsModel? = null,

    // Motivos Rápidos (Catálogo Tipo 20)
    val quickReasonCatalogs: List<AppCatalogModel> = emptyList(),
    val filteredQuickReasonCatalogs: List<AppCatalogModel> = emptyList(),
    val quickReasonSearchQuery: String = "",
    val isQuickReasonSheetOpen: Boolean = false,
    val showAddQuickReasonDialog: Boolean = false,
    val isLoadingAddQuickReason: Boolean = false
)
