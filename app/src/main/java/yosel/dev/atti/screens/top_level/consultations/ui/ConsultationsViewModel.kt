package yosel.dev.atti.screens.top_level.consultations.ui

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
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.top_level.consultations.domain.ConsultationsRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class ConsultationsViewModel @Inject constructor(
    private val repository: ConsultationsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ConsultationsState())
    private val _events = Channel<ConsultationsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val debouncedQuery = _state
        .map { it.searchQuery }
        .distinctUntilChanged()
        .debounce(300.milliseconds)

    private val consultationsFlow = combine(
        repository.getAllConsultationsWithDetails().catch {
            _events.send(ConsultationsEvent.ShowSnackBarError("Error al cargar las consultas locales"))
            emit(emptyList())
        },
        debouncedQuery
    ) { consultations, query ->
        val queryNorm = query.normalize()
        val filtered = consultations.filter { item ->
            if (queryNorm.isBlank()) return@filter true

            val patient = item.patientWithDetails.patient
            val client = item.patientWithDetails.client
            val type = item.consultationType

            val matchesPatient = patient.name.normalize().contains(queryNorm)
            val matchesBreed = patient.breed.normalize().contains(queryNorm)
            val matchesClient = "${client.firstName} ${client.lastName}".normalize().contains(queryNorm)
            val matchesType = type.name.normalize().contains(queryNorm)
            val matchesDoc = client.documentId.normalize().contains(queryNorm)

            matchesPatient || matchesBreed || matchesClient || matchesType || matchesDoc
        }
        consultations to filtered
    }

    val state: StateFlow<ConsultationsState> = combine(consultationsFlow, _state) { (consultations, filtered), localState ->
        localState.copy(
            consultations = consultations,
            filteredConsultations = filtered
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ConsultationsState()
    )

    init {
        syncRemoteConsultations()
    }

    fun onAction(action: ConsultationsAction) {
        when (action) {
            is ConsultationsAction.OnSearchQueryChange -> _state.update { it.copy(searchQuery = action.query) }
        }
    }

    private fun syncRemoteConsultations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.syncConsultations()
                .onSuccess { _state.update { it.copy(isLoading = false) } }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ConsultationsEvent.ShowSnackBarError("Error al sincronizar consultas"))
                }
        }
    }
}