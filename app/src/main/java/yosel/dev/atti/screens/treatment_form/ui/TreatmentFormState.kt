package yosel.dev.atti.screens.treatment_form.ui

import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.TreatmentWithDetailsModel

data class TreatmentFormState(
    val isEditMode: Boolean = false,
    val treatmentId: String? = null,
    val currentTab: TreatmentTab = TreatmentTab.PRODUCTS,
    val formInputState: TreatmentFormInputsState = TreatmentFormInputsState(),
    val initialFormInputState: TreatmentFormInputsState = TreatmentFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetData: Boolean = false,
    val isLoadingSaveTreatment: Boolean = false,
    val isLoadingUpdateTreatment: Boolean = false,
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    val existingTreatmentsWithDetails: List<TreatmentWithDetailsModel> = emptyList(),

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
