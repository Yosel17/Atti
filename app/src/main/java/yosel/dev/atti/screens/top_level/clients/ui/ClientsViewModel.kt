package yosel.dev.atti.screens.top_level.clients.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.models.filter.DateSortOrder
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.top_level.clients.domain.ClientsRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class ClientsViewModel @Inject constructor(
    private val repository: ClientsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ClientsState())
    private val _events = Channel<ClientsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val debouncedQuery = _state
        .map { it.searchQuery }
        .distinctUntilChanged()
        .debounce(300.milliseconds)

    private val filterFlow = _state
        .map { it.filter }
        .distinctUntilChanged()

    private val clientsListFlow = combine(
        repository.getAllClients().catch {
            _events.send(ClientsEvent.ShowSnackBarError("Error al cargar clientes locales"))
            emit(emptyList())
        },
        debouncedQuery,
        filterFlow
    ) { clients, query, filter ->
        val queryNorm = query.normalize()
        val filtered = clients
            .filter { client ->
                val matchesQuery = queryNorm.isBlank() ||
                        client.firstName.normalize().contains(queryNorm) ||
                        client.lastName.normalize().contains(queryNorm) ||
                        client.phoneNumber.normalize().contains(queryNorm) ||
                        client.documentId.normalize().contains(queryNorm)
                val matchesStatus = filter.status.statusCode == null || client.status == filter.status.statusCode
                matchesQuery && matchesStatus
            }
            .let { list ->
                when (filter.dateSort) {
                    DateSortOrder.NEWEST -> list.sortedByDescending { it.createdAt }
                    DateSortOrder.OLDEST -> list.sortedBy { it.createdAt }
                }
            }
        clients to filtered
    }

    val state: StateFlow<ClientsState> = combine(clientsListFlow, _state) { (clients, filtered), localState ->
        localState.copy(
            clients = clients,
            filteredClients = filtered
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ClientsState()
    )

    init {
        syncRemoteClients()
    }

    fun onAction(action: ClientsAction) {
        when (action) {
            is ClientsAction.OnSearchQueryChange -> _state.update { it.copy(searchQuery = action.query) }
            is ClientsAction.OnApplyFilter -> _state.update { it.copy(filter = action.filter) }
            is ClientsAction.OnToggleFilterSheet -> _state.update { it.copy(showFilterSheet = action.isOpen) }
            is ClientsAction.OnCallClick -> viewModelScope.launch {
                _events.send(ClientsEvent.NavigateToPhone(action.phoneNumber))
            }
            is ClientsAction.OnWhatsappClick -> viewModelScope.launch {
                _events.send(ClientsEvent.NavigateToWhatsapp(action.phoneNumber))
            }
        }
    }

    private fun syncRemoteClients() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.syncClients()
                .onSuccess { _state.update { it.copy(isLoading = false) } }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ClientsEvent.ShowSnackBarError("Error al sincronizar clientes"))
                }
        }
    }
}