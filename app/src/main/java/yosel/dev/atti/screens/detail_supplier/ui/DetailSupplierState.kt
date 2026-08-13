package yosel.dev.atti.screens.detail_supplier.ui

import yosel.dev.atti.core.models.model.SupplierModel

data class DetailSupplierState(
    val isLoading: Boolean = true,
    val supplier: SupplierModel = SupplierModel(),
    val isEditing: Boolean = false,
    val isLoadingUpdate: Boolean = false,
    val editFormState: EditSupplierFormState = EditSupplierFormState(),
    val initialEditFormState: EditSupplierFormState = EditSupplierFormState(),
    val showDialogConfirmDelete: Boolean = false,
    val isLoadingDeleteSupplier: Boolean = false,
    val showDialogConfirmRestore: Boolean = false,
    val isLoadingRestoreSupplier: Boolean = false,
    val showDialogInformation: Boolean = false
)
