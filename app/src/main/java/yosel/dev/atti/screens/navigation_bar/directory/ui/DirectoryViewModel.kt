package yosel.dev.atti.screens.navigation_bar.directory.ui

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
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.navigation_bar.directory.domain.DirectoryRepository
import java.text.Normalizer
import javax.inject.Inject

@HiltViewModel
class DirectoryViewModel @Inject constructor(
    private val repository: DirectoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DirectoryState())

    val state: StateFlow<DirectoryState> = combine(
        repository.getAllClients().catch {
            _events.send(DirectoryEvent.ShowSnackBarError("Error al obtener los clientes locales"))
        },
        repository.getAllPatients().catch {
            _events.send(DirectoryEvent.ShowSnackBarError("Error al obtener los pacientes locales"))
        },
        _state
    ) { clients, patients, localState ->
        val clientQueryNormalized = localState.clientSearchQuery.normalize()
        val patientQueryNormalized = localState.patientSearchQuery.normalize()

        val filteredClients = if (clientQueryNormalized.isBlank()) {
            clients
        } else {
            clients.filter { client ->
                client.firstName.normalize().contains(clientQueryNormalized) ||
                        client.lastName.normalize().contains(clientQueryNormalized) ||
                        client.phoneNumber.normalize().contains(clientQueryNormalized) ||
                        client.documentId.normalize().contains(clientQueryNormalized)
            }
        }

        val filteredPatients = if (patientQueryNormalized.isBlank()) {
            patients
        } else {
            patients.filter { patient ->
                patient.name.normalize().contains(patientQueryNormalized) ||
                        patient.breed.normalize().contains(patientQueryNormalized) ||
                        patient.color.normalize().contains(patientQueryNormalized)
            }
        }

        localState.copy(
            clients = clients,
            filteredClients = filteredClients,
            patients = patients,
            filteredPatients = filteredPatients
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
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
                onTabSelected(index = event.index)
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

            is DirectoryAction.OnClientSearchQueryChange -> {
                _state.update { it.copy(clientSearchQuery = event.query) }
            }

            is DirectoryAction.OnPatientSearchQueryChange -> {
                _state.update { it.copy(patientSearchQuery = event.query) }
            }
        }
    }

    private fun fetchRemoteClientsIfNeeded() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingClients = true) }
            repository.syncClients()
                .onSuccess {
                    _state.update { it.copy(isLoadingClients = false) }
                }
                .onFailure {
                    _state.update { it.copy(isLoadingClients = false) }
                    _events.send(
                        DirectoryEvent.ShowSnackBarError("Error al sincronizar los clientes")
                    )
                }
        }
    }

    private fun onTabSelected(index: Int) {
        _state.update { it.copy(selectedTabIndex = index) }
        if (index == 1 && _state.value.isFirstPatients) {
            fetchRemotePatientsIfNeeded()
        }
    }

    private fun fetchRemotePatientsIfNeeded() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingPatients = true) }
            repository.syncPatients()
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoadingPatients = false,
                            isFirstPatients = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoadingPatients = false,
                            isFirstPatients = false
                        )
                    }
                    _events.send(
                        DirectoryEvent.ShowSnackBarError("Error al sincronizar a los pacientes")
                    )
                }
        }
    }
}