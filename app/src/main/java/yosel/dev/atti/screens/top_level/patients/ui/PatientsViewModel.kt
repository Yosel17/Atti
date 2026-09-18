package yosel.dev.atti.screens.top_level.patients.ui

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
import yosel.dev.atti.screens.top_level.patients.domain.PatientsRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class PatientsViewModel @Inject constructor(
    private val repository: PatientsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PatientsState())
    private val _events = Channel<PatientsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val debouncedQuery = _state
        .map { it.searchQuery }
        .distinctUntilChanged()
        .debounce(300.milliseconds)

    private val filterFlow = _state
        .map { it.filter }
        .distinctUntilChanged()

    private val patientsListFlow = combine(
        repository.getAllPatientsWithCatalogs().catch {
            _events.send(PatientsEvent.ShowSnackBarError("Error al cargar pacientes locales"))
            emit(emptyList())
        },
        debouncedQuery,
        filterFlow
    ) { patients, query, filter ->
        val queryNorm = query.normalize()
        val filtered = patients
            .filter { patientWithCatalogs ->
                val p = patientWithCatalogs.patient
                val matchesQuery = queryNorm.isBlank() ||
                        p.name.normalize().contains(queryNorm) ||
                        p.breed.normalize().contains(queryNorm)
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

        val species = patients
            .map { it.species }
            .filter { it.id != 0 && it.name.isNotBlank() }
            .distinctBy { it.id }
        val genders = patients
            .map { it.gender }
            .filter { it.id != 0 && it.name.isNotBlank() }
            .distinctBy { it.id }

        Triple(patients, filtered, species to genders)
    }

    val state: StateFlow<PatientsState> = combine(patientsListFlow, _state) { (patients, filtered, catalogs), localState ->
        localState.copy(
            patients = patients,
            filteredPatients = filtered,
            availableSpecies = catalogs.first,
            availableGenders = catalogs.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PatientsState()
    )

    init {
        syncRemotePatients()
    }

    fun onAction(action: PatientsAction) {
        when (action) {
            is PatientsAction.OnSearchQueryChange -> _state.update { it.copy(searchQuery = action.query) }
            is PatientsAction.OnApplyFilter -> _state.update { it.copy(filter = action.filter) }
            is PatientsAction.OnToggleFilterSheet -> _state.update { it.copy(showFilterSheet = action.isOpen) }
        }
    }

    private fun syncRemotePatients() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.syncPatients()
                .onSuccess { _state.update { it.copy(isLoading = false) } }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(PatientsEvent.ShowSnackBarError("Error al sincronizar pacientes"))
                }
        }
    }
}