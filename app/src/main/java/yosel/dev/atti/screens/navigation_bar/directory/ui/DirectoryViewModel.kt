package yosel.dev.atti.screens.navigation_bar.directory.ui

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
import yosel.dev.atti.core.models.filter.NeuteredFilter
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.navigation_bar.directory.domain.DirectoryRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class DirectoryViewModel @Inject constructor(
    private val repository: DirectoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DirectoryState())

    private val debouncedClientQuery = _state
        .map { it.clientSearchQuery }
        .distinctUntilChanged()
        .debounce(300L.milliseconds)

    private val debouncedPatientQuery = _state
        .map { it.patientSearchQuery }
        .distinctUntilChanged()
        .debounce(300L.milliseconds)

    private val clientFilterFlow = _state
        .map { it.clientFilter }
        .distinctUntilChanged()

    private val patientFilterFlow = _state
        .map { it.patientFilter }
        .distinctUntilChanged()

    private val clientsFlow = combine(
        repository.getAllClients().catch {
            _events.send(DirectoryEvent.ShowSnackBarError("Error al obtener los clientes locales"))
        },
        debouncedClientQuery,
        clientFilterFlow
    ) { clients, query, filter ->
        val queryNormalized = query.normalize()
        val filtered = clients
            .filter { client ->
                val matchesQuery = queryNormalized.isBlank() ||
                        client.firstName.normalize().contains(queryNormalized) ||
                        client.lastName.normalize().contains(queryNormalized) ||
                        client.phoneNumber.normalize().contains(queryNormalized) ||
                        client.documentId.normalize().contains(queryNormalized)

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

    private val patientsFlow = combine(
        repository.getAllPatientsWithCatalogs().catch {
            _events.send(DirectoryEvent.ShowSnackBarError("Error al obtener los pacientes locales"))
        },
        debouncedPatientQuery,
        patientFilterFlow
    ) { patients, query, filter ->
        val queryNormalized = query.normalize()
        val filtered = patients
            .filter { patientWithCatalogs ->
                val p = patientWithCatalogs.patient
                val matchesQuery = queryNormalized.isBlank() ||
                        p.name.normalize().contains(queryNormalized) ||
                        p.breed.normalize().contains(queryNormalized)

                val matchesSpecies = filter.speciesId == null || p.speciesId == filter.speciesId
                val matchesGender = filter.genderId == null || p.genderId == filter.genderId
                val matchesNeutered = when (filter.neutered) {
                    NeuteredFilter.ALL -> true
                    NeuteredFilter.YES -> p.isNeutered
                    NeuteredFilter.NO -> !p.isNeutered
                }
                val matchesStatus = filter.status.statusCode == null || p.status == filter.status.statusCode

                matchesQuery && matchesSpecies && matchesGender && matchesNeutered && matchesStatus
            }
            .let { list ->
                when (filter.dateSort) {
                    DateSortOrder.NEWEST -> list.sortedByDescending { it.patient.createdAt }
                    DateSortOrder.OLDEST -> list.sortedBy { it.patient.createdAt }
                }
            }
        patients to filtered
    }

    val state: StateFlow<DirectoryState> = combine(
        clientsFlow,
        patientsFlow,
        _state
    ) { (clients, filteredClients), (patients, filteredPatients), localState ->
        localState.copy(
            clients = clients,
            filteredClients = filteredClients,
            patientsWithCatalogs = patients,
            filteredPatientsWithCatalogs = filteredPatients
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
            is DirectoryAction.OnTabSelected -> onTabSelected(index = event.index)
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
            is DirectoryAction.OnApplyClientFilter -> {
                _state.update { it.copy(clientFilter = event.filter) }
            }
            is DirectoryAction.OnApplyPatientFilter -> {
                _state.update { it.copy(patientFilter = event.filter) }
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
                        DirectoryEvent.ShowSnackBarError("Error al sincronizar a los clientes")
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
                .onFailure {
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