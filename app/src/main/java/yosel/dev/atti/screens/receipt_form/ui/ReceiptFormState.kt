package yosel.dev.atti.screens.receipt_form.ui

import yosel.dev.atti.core.models.model.ConsultationWithDetailsModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.ReceiptWithDetailsModel
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel

data class ReceiptFormState(
    val isEditMode: Boolean = false,
    val receiptId: Long? = null,
    val consultationId: String? = null,
    val hasConsultation: Boolean = false,
    val currentTab: ReceiptTab = ReceiptTab.PRODUCTS,
    val formInputState: ReceiptFormInputsState = ReceiptFormInputsState(),
    val initialFormInputState: ReceiptFormInputsState = ReceiptFormInputsState(),
    val isLoadingDataInitial: Boolean = true,
    val isSuccessGetData: Boolean = false,
    val isLoadingSaveReceipt: Boolean = false,
    val isLoadingUpdateReceipt: Boolean = false,
    val showDialogConfirm: Boolean = false,
    val consultationWithDetails: ConsultationWithDetailsModel = ConsultationWithDetailsModel(),
    val existingReceiptWithDetails: ReceiptWithDetailsModel? = null,

    // Catálogo Productos
    val productsWithDetails: List<ProductWithDetailsModel> = emptyList(),
    val filteredProducts: List<ProductWithDetailsModel> = emptyList(),
    val isProductSheetOpen: Boolean = false,
    val productSearchQuery: String = "",
    val tempSelectedProductIds: Set<String> = emptySet(),

    // Catálogo Servicios
    val servicesWithDetails: List<ServiceWithDetailsModel> = emptyList(),
    val filteredServices: List<ServiceWithDetailsModel> = emptyList(),
    val isServiceSheetOpen: Boolean = false,
    val serviceSearchQuery: String = "",
    val tempSelectedServiceIds: Set<String> = emptySet()
)
