package yosel.dev.atti.screens.clinical_exam_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClinicalExaminationModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel

data class ClinicalExamFormState(
    val isEditMode: Boolean = false,
    val examId: String? = null,
    val currentExam: ClinicalExaminationModel? = null,
    val initialFormInputState: ClinicalExamFormInputsState = ClinicalExamFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetCatalogs: Boolean = false,
    val isLoadingSaveExam: Boolean = false,
    val isLoadingUpdateExam: Boolean = false,
    val formInputState: ClinicalExamFormInputsState = ClinicalExamFormInputsState(),
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),

    // Catálogos Pelaje
    val coatCatalogs: List<AppCatalogModel> = emptyList(),
    val filteredCoatCatalogs: List<AppCatalogModel> = emptyList(),
    val coatSearchQuery: String = "",
    val isCoatSheetOpen: Boolean = false,

    // Catálogos Nódulos linfáticos
    val lymphNodeCatalogs: List<AppCatalogModel> = emptyList(),
    val filteredLymphNodeCatalogs: List<AppCatalogModel> = emptyList(),
    val lymphNodeSearchQuery: String = "",
    val isLymphNodeSheetOpen: Boolean = false,

    // Diálogo general para agregar catálogo
    val showAddAppCatalogDialog: Boolean = false,
    val activeCatalogTypeId: Int = 0,
    val activeCatalogTypeName: String = "",
    val isLoadingAddCatalog: Boolean = false
)
