package yosel.dev.atti.screens.auxiliary_test_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.AuxiliaryTestWithDetailsModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

data class AuxiliaryTestFormState(
    val isEditMode: Boolean = false,
    val auxiliaryTestId: String? = null,
    val initialFormInputState: AuxiliaryTestFormInputsState = AuxiliaryTestFormInputsState(),
    val formInputState: AuxiliaryTestFormInputsState = AuxiliaryTestFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetCatalogs: Boolean = false,
    val isLoadingSaveAuxiliaryTest: Boolean = false,
    val isLoadingUpdateAuxiliaryTest: Boolean = false,
    val isLoadingAddTag: Boolean = false,
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    val existingAuxiliaryTestsWithDetails: List<AuxiliaryTestWithDetailsModel> = emptyList(),
    // Catálogos Pruebas Auxiliares (Tipo 21)
    val auxiliaryTestCatalogs: List<AppCatalogModel> = emptyList(),
    val filteredAuxiliaryTestCatalogs: List<AppCatalogModel> = emptyList(),
    val searchQuery: String = ""
)
