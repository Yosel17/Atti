package yosel.dev.atti.screens.prescription_form.ui

import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PrescriptionWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel

data class PrescriptionFormState(
    val isEditMode: Boolean = false,
    val prescriptionId: String? = null,
    val formInputState: PrescriptionFormInputsState = PrescriptionFormInputsState(),
    val initialFormInputState: PrescriptionFormInputsState = PrescriptionFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetData: Boolean = false,
    val isLoadingSavePrescription: Boolean = false,
    val isLoadingUpdatePrescription: Boolean = false,
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    val existingPrescriptionWithDetails: PrescriptionWithDetailsModel? = null,

    // Inventario de productos
    val productsWithDetails: List<ProductWithDetailsModel> = emptyList(),
    val filteredProducts: List<ProductWithDetailsModel> = emptyList(),
    val isProductSheetOpen: Boolean = false,
    val productSearchQuery: String = "",
    val tempSelectedProductIds: Set<String> = emptySet(),

    // Catálogos Presets Rápidos (Tipo 19)
    val presetCatalogs: List<AppCatalogModel> = emptyList(),
    val filteredPresetCatalogs: List<AppCatalogModel> = emptyList(),
    val presetSearchQuery: String = "",
    val isPresetSheetOpen: Boolean = false,
    val targetPresetItemId: String? = null,
    val showAddPresetDialog: Boolean = false,
    val isLoadingAddPreset: Boolean = false,

    // Diálogo producto fuera de inventario
    val showAddCustomProductDialog: Boolean = false
)
