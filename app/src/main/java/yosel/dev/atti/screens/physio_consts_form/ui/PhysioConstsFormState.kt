package yosel.dev.atti.screens.physio_consts_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PhysiologicalConstsModel

data class PhysioConstsFormState(
    val isEditMode: Boolean = false,
    val constsId: String? = null,
    val currentConstants: PhysiologicalConstsModel? = null,
    val initialFormInputState: PhysioConstsFormInputsState = PhysioConstsFormInputsState(),
    val formInputState: PhysioConstsFormInputsState = PhysioConstsFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetCatalogs: Boolean = false,
    val isLoadingSave: Boolean = false,
    val isLoadingUpdate: Boolean = false,
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    // Unidades de peso
    val weightUnits: List<AppCatalogModel> = emptyList(),
    val filteredWeightUnits: List<AppCatalogModel> = emptyList(),
    val weightUnitSearchQuery: String = "",
    val isWeightUnitSheetOpen: Boolean = false,
    // Diálogo nuevo catálogo
    val showAddAppCatalogDialog: Boolean = false,
    val activeCatalogTypeId: Int = 0,
    val activeCatalogTypeName: String = "",
    val isLoadingAddCatalog: Boolean = false
)