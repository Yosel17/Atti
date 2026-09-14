package yosel.dev.atti.screens.shift_medication_form.ui

import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.ShiftMedicationWithDetailsModel

data class ShiftMedicationFormState(
    val isEditMode: Boolean = false,
    val shiftMedicationId: String? = null,
    val currentTab: ShiftMedicationTab = ShiftMedicationTab.PRODUCTS,
    val formInputState: ShiftMedicationFormInputsState = ShiftMedicationFormInputsState(),
    val initialFormInputState: ShiftMedicationFormInputsState = ShiftMedicationFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetData: Boolean = false,
    val isLoadingSaveShiftMedication: Boolean = false,
    val isLoadingUpdateShiftMedication: Boolean = false,
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    val existingShiftMedicationsWithDetails: List<ShiftMedicationWithDetailsModel> = emptyList(),

    // Productos
    val productsWithDetails: List<ProductWithDetailsModel> = emptyList(),
    val filteredProducts: List<ProductWithDetailsModel> = emptyList(),
    val isProductSheetOpen: Boolean = false,
    val productSearchQuery: String = "",
    val tempSelectedProductIds: Set<String> = emptySet(),

    // Servicios
    val servicesWithDetails: List<ServiceWithDetailsModel> = emptyList(),
    val filteredServices: List<ServiceWithDetailsModel> = emptyList(),
    val isServiceSheetOpen: Boolean = false,
    val serviceSearchQuery: String = "",
    val tempSelectedServiceIds: Set<String> = emptySet()
)
