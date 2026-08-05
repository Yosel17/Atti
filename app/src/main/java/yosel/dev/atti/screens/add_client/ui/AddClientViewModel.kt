package yosel.dev.atti.screens.add_client.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.add_client.domain.AddClientRepository
import javax.inject.Inject

@HiltViewModel
class AddClientViewModel @Inject constructor(
    private val repository: AddClientRepository
): ViewModel() {

    private val _state = MutableStateFlow(AddClientState())
    val state: StateFlow<AddClientState> = _state

    private val _eventChannel = Channel<AddClientEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onAction(action: AddClientAction){
        when(action){
            is AddClientAction.OnChangeValueFormState ->{
                onValueFormStateChange(action.value, action.field)
            }

            AddClientAction.AddClient -> addClient()
        }
    }

    private fun onValueFormStateChange(value: String, field: Int) {
        when(field){
            Constants.FIRST_NAME_FIELD -> _state.update { it.copy(formState = it.formState.copy(firstName = value)) }
            Constants.LAST_NAME_FIELD -> _state.update { it.copy(formState = it.formState.copy(lastName = value)) }
            Constants.DOCUMENT_ID_FIELD -> _state.update { it.copy(formState = it.formState.copy(documentId = value)) }
            Constants.PHONE_NUMBER_FIELD -> _state.update { it.copy(formState = it.formState.copy(phoneNumber = value)) }
            Constants.EMAIL_FIELD -> _state.update { it.copy(formState = it.formState.copy(email = value)) }
            Constants.ADDRESS_FIELD -> _state.update { it.copy(formState = it.formState.copy(address = value)) }
        }
    }

    private fun addClient() {
        val cs = _state.value
        if (!cs.formState.isValid) return

        _state.update { it.copy(isLoadingAddClient = true) }

        val client = cs.formState.toModel()

        viewModelScope.launch {
            repository.insertClient(client = client)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoadingAddClient = false,
                            formState = AddClientFormState()
                        )
                    }
                    _eventChannel.send(
                        element = AddClientEvent.ShowSuccessSnackbar(
                            "Cliente guardado correctamente."
                        )
                    )
                }.onFailure {
                    _state.update {
                        it.copy(isLoadingAddClient = false)
                    }
                    _eventChannel.send(
                        element = AddClientEvent.ShowErrorSnackbar(
                            "No pudimos guardar al nuevo cliente. Inténtalo de nuevo."
                        )
                    )
                }
        }
    }
}