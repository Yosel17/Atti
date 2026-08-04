package yosel.dev.atti.screens.clients.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.screens.clients.domain.DirectoryRepository
import javax.inject.Inject

@HiltViewModel
class DirectoryViewModel @Inject constructor(
    private val repository: DirectoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DirectoryState())
    val state: StateFlow<DirectoryState> = repository.getAllClients()
        .catch { error ->
            _events.send(DirectoryEvent.ShowSnackBarError("Error al obtener los clientes locales"))
        }
        .combine(_state) { clients, localState ->
            localState.copy(
                clients = clients
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = DirectoryState()
        )

    private val _events = Channel<DirectoryEvent>()
    val events = _events.receiveAsFlow()

    init {
        fetchRemoteClientsIfNeeded()
    }

    fun onAction(event: DirectoryAction) {
        when (event) {
            is DirectoryAction.OnTabSelected -> {
                _state.update { it.copy(selectedTabIndex = event.index) }
            }
            is DirectoryAction.OnCallClick -> {
                viewModelScope.launch {
                    _events.send(DirectoryEvent.NavigateToPhone(event.phoneNumber))
                }
            }
            is DirectoryAction.OnWhatsappClick -> {
                viewModelScope.launch {
                    _events.send(DirectoryEvent.NavigateToWhatsapp(event.phoneNumber))
                }
            }
        }
    }

    private fun fetchRemoteClientsIfNeeded() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingClients = _state.value.clients.isEmpty()) }
            repository.syncClients()
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoadingClients = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoadingClients = false) }
                    _events.send(
                        DirectoryEvent.ShowSnackBarError(
                            message = "Error al sincronizar los clientes"
                        )
                    )
                }
        }
    }
}