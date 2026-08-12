package yosel.dev.atti.screens.add_supplier.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.add_supplier.domain.AddSupplierRepository
import javax.inject.Inject

@HiltViewModel
class AddSupplierViewModel @Inject constructor(
    private val repository: AddSupplierRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddSupplierState())
    val state: StateFlow<AddSupplierState> = _state

    private val _eventChannel = Channel<AddSupplierEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onAction(action: AddSupplierAction) {
        when (action) {
            is AddSupplierAction.OnChangeValueFormState -> {
                onValueFormStateChange(action.value, action.field)
            }
            AddSupplierAction.AddSupplier -> addSupplier()
        }
    }

    private fun onValueFormStateChange(value: String, field: Int) {
        _state.update { currentState ->
            val newFormState = when (field) {
                Constants.SUPPLIER_NAME_FIELD -> currentState.formState.copy(name = value)
                Constants.SUPPLIER_TAX_ID_FIELD -> currentState.formState.copy(taxId = value)
                Constants.SUPPLIER_PHONE_FIELD -> currentState.formState.copy(phoneNumber = value)
                Constants.SUPPLIER_ADDRESS_FIELD -> currentState.formState.copy(address = value)
                else -> currentState.formState
            }
            currentState.copy(
                formState = newFormState.copy(
                    touchedFields = newFormState.touchedFields + field
                )
            )
        }
    }

    private fun addSupplier() {
        val currentState = _state.value
        if (!currentState.formState.isValid) {
            _state.update {
                it.copy(
                    formState = it.formState.copy(
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

        _state.update { it.copy(isLoadingAddSupplier = true) }

        val supplier = SupplierModel(
            name = currentState.formState.name.trim(),
            taxId = currentState.formState.taxId.trim(),
            phoneNumber = currentState.formState.phoneNumber.trim(),
            address = currentState.formState.address.trim()
        )

        viewModelScope.launch {
            repository.insertSupplier(supplier = supplier)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoadingAddSupplier = false,
                            formState = AddSupplierFormState() // Se limpian los inputs
                        )
                    }
                    _eventChannel.send(
                        AddSupplierEvent.ShowSuccessSnackbar("Proveedor guardado correctamente.")
                    )
                }
                .onFailure {
                    _state.update { it.copy(isLoadingAddSupplier = false) }
                    _eventChannel.send(
                        AddSupplierEvent.ShowErrorSnackbar("No pudimos guardar al proveedor. Inténtalo de nuevo.")
                    )
                }
        }
    }
}