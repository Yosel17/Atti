package yosel.dev.atti.screens.detail_supplier.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toEditFormState
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.detail_supplier.domain.DetailSupplierRepository

@HiltViewModel(assistedFactory = DetailSupplierViewModel.Factory::class)
class DetailSupplierViewModel @AssistedInject constructor(
    private val repository: DetailSupplierRepository,
    @Assisted private val supplierId: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(supplierId: String): DetailSupplierViewModel
    }

    private val _state = MutableStateFlow(DetailSupplierState())
    val state: StateFlow<DetailSupplierState> = _state

    private val _eventChannel = Channel<DetailSupplierEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        observeSupplier()
    }

    private fun observeSupplier() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getSupplierByIdFlow(supplierId)
                .catch {
                    _state.update { currentState -> currentState.copy(isLoading = false) }
                    _eventChannel.send(
                        DetailSupplierEvent.ShowErrorSnackbar(
                            message = "No pudimos cargar la información del proveedor."
                        )
                    )
                }
                .collectLatest { supplierModel ->
                    _state.update { currentState ->
                        currentState.copy(
                            supplier = supplierModel ?: currentState.supplier,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onAction(action: DetailSupplierAction) {
        when (action) {
            is DetailSupplierAction.OnCallClick -> {
                viewModelScope.launch {
                    _eventChannel.send(DetailSupplierEvent.OnCallClick(phoneNumber = action.phoneNumber))
                }
            }
            is DetailSupplierAction.OnWhatsappClick -> {
                viewModelScope.launch {
                    _eventChannel.send(DetailSupplierEvent.OnWhatsappClick(phoneNumber = action.phoneNumber))
                }
            }
            DetailSupplierAction.OnEditClick -> {
                _state.update {
                    val editForm = it.supplier.toEditFormState()
                    it.copy(
                        isEditing = true,
                        editFormState = editForm,
                        initialEditFormState = editForm
                    )
                }
            }
            DetailSupplierAction.OnDismissEdit -> {
                _state.update { it.copy(isEditing = false) }
            }
            is DetailSupplierAction.OnChangeEditFormValue -> {
                onValueEditFormChange(action.value, action.field)
            }
            DetailSupplierAction.OnUpdateSupplier -> updateSupplier()
            DetailSupplierAction.DeleteSupplier -> deleteSupplier()
            DetailSupplierAction.RestoreSupplier -> restoreSupplier()
            is DetailSupplierAction.ToggleShowDialogConfirmDelete -> {
                _state.update { it.copy(showDialogConfirmDelete = action.show) }
            }
            is DetailSupplierAction.ToggleShowDialogConfirmRestore -> {
                _state.update { it.copy(showDialogConfirmRestore = action.show) }
            }
            is DetailSupplierAction.ToggleShowDialogInformation -> {
                _state.update { it.copy(showDialogInformation = action.show) }
            }
        }
    }

    private fun onValueEditFormChange(value: String, field: Int) {
        _state.update {
            val newFormState = when (field) {
                Constants.SUPPLIER_NAME_FIELD -> it.editFormState.copy(name = value)
                Constants.SUPPLIER_TAX_ID_FIELD -> it.editFormState.copy(taxId = value)
                Constants.SUPPLIER_PHONE_FIELD -> it.editFormState.copy(phoneNumber = value)
                Constants.SUPPLIER_ADDRESS_FIELD -> it.editFormState.copy(address = value)
                else -> it.editFormState
            }
            it.copy(editFormState = newFormState.copy(touchedFields = newFormState.touchedFields + field))
        }
    }

    private fun updateSupplier() {
        val currentState = _state.value
        if (!currentState.editFormState.isValid) {
            _state.update {
                it.copy(
                    editFormState = it.editFormState.copy(
                        touchedFields = setOf(
                            Constants.SUPPLIER_NAME_FIELD,
                            Constants.SUPPLIER_TAX_ID_FIELD,
                            Constants.SUPPLIER_PHONE_FIELD,
                            Constants.SUPPLIER_ADDRESS_FIELD
                        )
                    )
                )
            }
            return
        }
        _state.update { it.copy(isLoadingUpdate = true) }
        val updatedSupplier = currentState.editFormState.toModel(
            status = currentState.supplier.status
        )
        viewModelScope.launch {
            repository.updateSupplier(supplier = updatedSupplier)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoadingUpdate = false,
                            isEditing = false
                        )
                    }
                    _eventChannel.send(DetailSupplierEvent.ShowSuccessSnackbar("Información actualizada correctamente."))
                }
                .onFailure {
                    _state.update { it.copy(isLoadingUpdate = false) }
                    _eventChannel.send(DetailSupplierEvent.ShowErrorSnackbar("No se pudo actualizar la información. Inténtalo de nuevo."))
                }
        }
    }

    private fun deleteSupplier() {
        val cs = _state.value
        _state.update { it.copy(isLoadingDeleteSupplier = true) }
        viewModelScope.launch {
            repository.updateSupplierStatus(
                supplierId = cs.supplier.id,
                newStatus = Constants.DELETED_STATUS
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoadingDeleteSupplier = false,
                            showDialogConfirmDelete = false
                        )
                    }
                    _eventChannel.send(
                        DetailSupplierEvent.ShowSuccessSnackbar(message = "Proveedor eliminado exitosamente")
                    )
                },
                onFailure = {
                    _state.update {
                        it.copy(isLoadingDeleteSupplier = false, showDialogConfirmDelete = false)
                    }
                    _eventChannel.send(
                        DetailSupplierEvent.ShowErrorSnackbar(message = "No se pudo eliminar al proveedor")
                    )
                }
            )
        }
    }

    private fun restoreSupplier() {
        val cs = _state.value
        _state.update { it.copy(isLoadingRestoreSupplier = true) }
        viewModelScope.launch {
            repository.updateSupplierStatus(
                supplierId = cs.supplier.id,
                newStatus = Constants.ACTIVE_STATUS
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoadingRestoreSupplier = false,
                            showDialogConfirmRestore = false
                        )
                    }
                    _eventChannel.send(
                        DetailSupplierEvent.ShowSuccessSnackbar(message = "Proveedor restaurado exitosamente")
                    )
                },
                onFailure = {
                    _state.update {
                        it.copy(isLoadingRestoreSupplier = false, showDialogConfirmRestore = false)
                    }
                    _eventChannel.send(
                        DetailSupplierEvent.ShowErrorSnackbar(message = "No se pudo restaurar al proveedor")
                    )
                }
            )
        }
    }
}