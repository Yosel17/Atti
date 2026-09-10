package yosel.dev.atti.screens.pre_anesthetic_test_form.ui

import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.PreAnestheticTestWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

data class PreAnestheticTestFormState(
    val isEditMode: Boolean = false,
    val preAnestheticTestId: String? = null,
    val currentTab: PreAnestheticTestTab = PreAnestheticTestTab.PRODUCTS,
    val formInputState: PreAnestheticTestFormInputsState = PreAnestheticTestFormInputsState(),
    val initialFormInputState: PreAnestheticTestFormInputsState = PreAnestheticTestFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetData: Boolean = false,
    val isLoadingSavePreAnestheticTest: Boolean = false,
    val isLoadingUpdatePreAnestheticTest: Boolean = false,
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    val existingPreAnestheticTestsWithDetails: List<PreAnestheticTestWithDetailsModel> = emptyList(),

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
