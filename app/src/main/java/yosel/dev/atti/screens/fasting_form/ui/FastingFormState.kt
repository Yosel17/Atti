package yosel.dev.atti.screens.fasting_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.FastingWithDetailsModel

data class FastingFormState(
    val fastingId: String? = null,
    val existingFastingWithDetails: FastingWithDetailsModel? = null,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),

    val foodCatalogs: List<AppCatalogModel> = emptyList(),
    val filteredFoodCatalogs: List<AppCatalogModel> = emptyList(),
    val waterCatalogs: List<AppCatalogModel> = emptyList(),
    val filteredWaterCatalogs: List<AppCatalogModel> = emptyList(),

    val formInputState: FastingFormInputsState = FastingFormInputsState(),
    val initialFormInputState: FastingFormInputsState = FastingFormInputsState(),

    val isEditMode: Boolean = false,
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetCatalogs: Boolean = false,

    val isLoadingSaveFasting: Boolean = false,
    val isLoadingUpdateFasting: Boolean = false,
    val showDialogConfirm: Boolean = false,

    // Sheets
    val isFoodSheetOpen: Boolean = false,
    val foodSearchQuery: String = "",
    val isWaterSheetOpen: Boolean = false,
    val waterSearchQuery: String = "",

    val showAddAppCatalogDialog: Boolean = false,
    val isLoadingAddCatalog: Boolean = false,
    val activeCatalogTypeId: Int = 0,
    val activeCatalogTypeName: String = ""
)