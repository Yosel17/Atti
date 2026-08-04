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
): ViewModel() {

    private val _state = MutableStateFlow(DirectoryState())
    val state: StateFlow<DirectoryState> = repository.getAllClients()
        .catch { error ->
            _events.send(DirectoryEvent.ShowSnackBarError("Error al obtener a los clientes"))
        }.combine(_state){ clients, localState ->
            localState.copy(
                clients = clients
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DirectoryState(isLoading = true)
        )

    private val _events = Channel<DirectoryEvent>()
    val events = _events.receiveAsFlow()

    init {
        fetchRemoteClients()
    }

    private fun fetchRemoteClients() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.syncClients()
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                }.onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(
                        DirectoryEvent.ShowSnackBarError(
                            message = error.localizedMessage ?: "Error al sincronizar los clientes"
                        )
                    )
                }
        }
    }
}