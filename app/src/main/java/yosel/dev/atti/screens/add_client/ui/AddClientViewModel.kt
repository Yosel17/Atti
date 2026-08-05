package yosel.dev.atti.screens.add_client.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.add_client.domain.AddClientRepository
import javax.inject.Inject

@HiltViewModel
class AddClientViewModel @Inject constructor(
    private val repository: AddClientRepository
): ViewModel() {

    private val _state = MutableStateFlow(AddClientState())
    val state: StateFlow<AddClientState> = _state

    fun onAction(action: AddClientAction){
        when(action){
            is AddClientAction.OnChangeValueFormState ->{
                onValueFormStateChange(action.value, action.field)
            }
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
}