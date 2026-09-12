package yosel.dev.atti.screens.consent_form.ui

import yosel.dev.atti.core.models.model.ConsentModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

data class ConsentFormState(
    val isEditMode: Boolean = false,
    val consentId: String? = null,
    val formInputState: ConsentFormInputsState = ConsentFormInputsState(),
    val initialFormInputState: ConsentFormInputsState = ConsentFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetData: Boolean = false,
    val isLoadingSaveConsent: Boolean = false,
    val isLoadingUpdateConsent: Boolean = false,
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    val existingConsent: ConsentModel? = null,
    val isBottomSheetVisible: Boolean = false,
    val showRationaleDialog: Boolean = false,
    val showSettingsDialog: Boolean = false
)
