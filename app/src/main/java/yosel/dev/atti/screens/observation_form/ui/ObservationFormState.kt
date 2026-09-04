package yosel.dev.atti.screens.observation_form.ui

import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.ObservationModel

data class ObservationFormState(
    val isEditMode: Boolean = false,
    val observationId: String? = null,
    val formInputState: ObservationFormInputsState = ObservationFormInputsState(),
    val initialFormInputState: ObservationFormInputsState = ObservationFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetData: Boolean = false,
    val isLoadingSaveObservation: Boolean = false,
    val isLoadingUpdateObservation: Boolean = false,
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    val existingObservation: ObservationModel? = null
)
