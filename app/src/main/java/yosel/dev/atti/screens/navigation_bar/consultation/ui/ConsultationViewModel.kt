package yosel.dev.atti.screens.navigation_bar.consultation.ui

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
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.PatientWithCatalogsModel
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.navigation_bar.consultation.domain.ConsultationRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class ConsultationViewModel @Inject constructor(
    private val repository: ConsultationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ConsultationState())

    private val debouncedPatientQuery = _state
        .map { it.patientSearchQuery }
        .distinctUntilChanged()
        .debounce(300L.milliseconds)

    private val patientsFlow = combine(
        repository.getAllPatientsWithCatalogsFlow().catch {
            _events.send(ConsultationEvent.ShowSnackBarError("Error al cargar los pacientes"))
        },
        debouncedPatientQuery
    ) { patients, query ->
        val queryNormalized = query.normalize()
        val filtered = if (queryNormalized.isBlank()) {
            patients
        } else {
            patients.filter { item ->
                item.patient.name.normalize().contains(queryNormalized) ||
                        item.patient.breed.normalize().contains(queryNormalized) ||
                        item.species.name.normalize().contains(queryNormalized)
            }
        }
        patients to filtered
    }

    val state: StateFlow<ConsultationState> = combine(
        patientsFlow,
        repository.getActiveConsultationFlow().catch {
            _events.send(ConsultationEvent.ShowSnackBarError("Error al cargar la consulta activa"))
        },
        _state
    ) { (patients, filteredPatients), activeConsultation, localState ->
        val resolvedSelectedPatient = if (activeConsultation != null) {
            patients.find { it.patient.id == activeConsultation.patient.id }
                ?: PatientWithCatalogsModel(patient = activeConsultation.patient)
        } else {
            localState.selectedPatient
        }

        val resolvedSelectedReason = if (activeConsultation != null) {
            localState.consultationReasons.find { it.id == activeConsultation.consultationType.id }
                ?: activeConsultation.consultationType
        } else {
            localState.selectedReason
        }

        localState.copy(
            patients = patients,
            filteredPatients = filteredPatients,
            activeConsultation = activeConsultation,
            selectedPatient = resolvedSelectedPatient,
            selectedReason = resolvedSelectedReason
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ConsultationState()
    )

    private val _events = Channel<ConsultationEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadInitialData()
    }

    fun onAction(action: ConsultationAction) {
        when (action) {
            is ConsultationAction.OnSearchPatientQueryChange -> {
                _state.update { it.copy(patientSearchQuery = action.query) }
            }
            is ConsultationAction.OnSelectPatient -> {
                if (_state.value.hasActiveConsultation) return
                _state.update {
                    it.copy(
                        selectedPatient = if (it.selectedPatient?.patient?.id == action.patient.patient.id) null else action.patient
                    )
                }
            }
            is ConsultationAction.OnSelectConsultationReason -> {
                if (_state.value.hasActiveConsultation) return
                handleReasonSelection(action.reason)
            }
            ConsultationAction.OnConfirmStartConsultation -> startConsultation()
            ConsultationAction.OnDismissConfirmDialog -> {
                _state.update { it.copy(showConfirmDialog = false, pendingSelectedReason = null) }
            }
            ConsultationAction.OnRetryInitialData -> loadInitialData()
        }
    }

    private fun handleReasonSelection(reason: AppCatalogModel) {
        val currentPatient = _state.value.selectedPatient
        if (currentPatient == null) {
            viewModelScope.launch {
                _events.send(ConsultationEvent.ShowSnackBarError("Primero selecciona un paciente"))
            }
            return
        }

        _state.update {
            it.copy(
                pendingSelectedReason = reason,
                showConfirmDialog = true
            )
        }
    }

    private fun startConsultation() {
        val currentPatient = _state.value.selectedPatient ?: return
        val pendingReason = _state.value.pendingSelectedReason ?: return

        viewModelScope.launch {
            _state.update { it.copy(isStartingConsultation = true, showConfirmDialog = false) }

            repository.createConsultation(
                patientId = currentPatient.patient.id,
                consultationTypeId = pendingReason.id
            ).onSuccess {
                _state.update {
                    it.copy(
                        isStartingConsultation = false,
                        selectedReason = pendingReason,
                        pendingSelectedReason = null
                    )
                }
                _events.send(ConsultationEvent.ShowSnackBarSuccess("Consulta iniciada exitosamente"))
            }.onFailure {
                _state.update { it.copy(isStartingConsultation = false, pendingSelectedReason = null) }
                _events.send(ConsultationEvent.ShowSnackBarError("No se pudo iniciar la consulta"))
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingData = true) }

            repository.syncPatients()
            repository.syncActiveConsultation()

            repository.getConsultationReasons()
                .onSuccess { reasons ->
                    _state.update {
                        it.copy(
                            consultationReasons = reasons,
                            isLoadingData = false
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoadingData = false) }
                    _events.send(ConsultationEvent.ShowSnackBarError("Error al cargar los motivos de consulta"))
                }
        }
    }
}