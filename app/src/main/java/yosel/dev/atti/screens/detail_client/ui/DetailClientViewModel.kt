package yosel.dev.atti.screens.detail_client.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.screens.detail_client.domain.DetailClientRepository
import javax.inject.Inject

@HiltViewModel
class DetailClientViewModel @Inject constructor(
    private val repository: DetailClientRepository,
    @Assisted private val clienteId: String,
    @Assisted private val isLocalPatients: Boolean,
): ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(clienteId: String, isLocalPatients: Boolean): DetailClientViewModel
    }

    private val _state = MutableStateFlow(DetailClientState())
    val state: StateFlow<DetailClientState> = _state

    private val _eventChannel = Channel<DetailClientEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        getClientWithPatients(clientId = clienteId, isLocalPatients = isLocalPatients)
    }

    fun onAction(action: DetailClientAction){
        when(action){
            else -> {}
        }
    }

    private fun getClientWithPatients(clientId: String, isLocalPatients: Boolean) {
        viewModelScope.launch {
            repository.getClientWithPatients(clientId = clientId, isLocalPatients = isLocalPatients)
                .onSuccess { clientWithPatientsModel ->
                    _state.update {
                        it.copy(
                            clientWithPatients = clientWithPatientsModel,
                            isLoading = false
                        )
                    }
                }.onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _eventChannel.send(
                        DetailClientEvent.ShowErrorSnackbar(
                            message = "No pudimos cargar la información del cliente. Inténtalo de nuevo."
                        )
                    )
                }
        }
    }
}