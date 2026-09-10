package yosel.dev.atti.screens.asa_classification_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.AsaClassificationWithDetailsModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

data class AsaClassificationFormState(
    val isEditMode: Boolean = false,
    val asaClassificationId: String? = null,
    val initialFormInputState: AsaClassificationFormInputsState = AsaClassificationFormInputsState(),
    val formInputState: AsaClassificationFormInputsState = AsaClassificationFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetCatalogs: Boolean = false,
    val isLoadingSaveAsaClassification: Boolean = false,
    val isLoadingUpdateAsaClassification: Boolean = false,
    val isLoadingAddTag: Boolean = false,
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    val existingAsaClassificationsWithDetails: List<AsaClassificationWithDetailsModel> = emptyList(),
    // Catálogos Clasificación ASA (Tipo 24)
    val asaClassificationCatalogs: List<AppCatalogModel> = emptyList(),
    val filteredAsaClassificationCatalogs: List<AppCatalogModel> = emptyList(),
    val searchQuery: String = ""
)
