package yosel.dev.atti.screens.detail_patient.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientWithDetailsModel
import yosel.dev.atti.core.navigation.main.Screens.*
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.detail_patient.domain.DetailPatientRepository
import yosel.dev.atti.screens.detail_patient.ui.DetailPatientEvent.*

@HiltViewModel(assistedFactory = DetailPatientViewModel.Factory::class)
class DetailPatientViewModel @AssistedInject constructor(
    private val repository: DetailPatientRepository,
    @Assisted private val patientId: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(patientId: String): DetailPatientViewModel
    }

    private val _state = MutableStateFlow(DetailPatientState())
    val state: StateFlow<DetailPatientState> = _state

    private val _eventChannel = Channel<DetailPatientEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var lastFetchedClientId: String? = null

    fun onAction(action: DetailPatientAction) {
        when (action) {
            is DetailPatientAction.ToggleShowBottomSheetDelete -> {
                _state.update {
                    it.copy(
                        showBottomSheetDelete = action.show,
                        isDeleteSuccess = false, // Reiniciar estado de éxito
                        deleteComment = if (action.show) "" else it.deleteComment
                    )
                }
            }
            is DetailPatientAction.OnDeleteCommentChange -> {
                _state.update { it.copy(deleteComment = action.comment) }
            }
            DetailPatientAction.OnEditClick -> {
                viewModelScope.launch {
                    _eventChannel.send(OnNavigationMain(AddPatient(patientId)))
                }
            }
            DetailPatientAction.DeletePatient -> deletePatient()
            DetailPatientAction.RestorePatient -> restorePatient()
            is DetailPatientAction.ToggleShowDialogConfirmRestore -> {
                _state.update { it.copy(showDialogConfirmRestore = action.show) }
            }
            is DetailPatientAction.OnConsultationClick -> {
                viewModelScope.launch {
                    _eventChannel.send(
                        OnNavigationMain(
                            DetailConsultation(
                                consultationId = action.consultationId,
                                consultationTypeId = action.consultationTypeId
                            )
                        )
                    )
                }
            }
        }
    }

    init {
        observePatient()
        loadConsultations()
    }

    private fun loadConsultations() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingConsultations = true) }
            repository.getPatientConsultations(patientId).fold(
                onSuccess = { consultationsList ->
                    _state.update { currentState ->
                        currentState.copy(
                            consultations = consultationsList,
                            isLoadingConsultations = false
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingConsultations = false) }
                    _eventChannel.send(ShowErrorSnackbar("No se pudo recuperar el historial clínico del paciente"))
                }
            )
        }
    }

    private fun observePatient() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getPatientWithCatalogsByIdFlow(patientId)
                .catch {
                    _state.update { it.copy(isLoading = false) }
                    _eventChannel.send(ShowErrorSnackbar("Error al cargar el paciente"))
                }
                .collectLatest { patientWithCatalogsModel ->
                    _state.update { currentState ->
                        currentState.copy(
                            patientWithCatalogs = patientWithCatalogsModel ?: PatientWithDetailsModel(),
                            isLoading = false
                        )
                    }
                    val isValid = patientWithCatalogsModel?.patient?.clientId?.isNotBlank() ?: false
                    if (isValid) {
                        fetchClientIfNeeded(patientWithCatalogsModel.patient.clientId)
                    }
                }
        }
    }

    private suspend fun fetchClientIfNeeded(clientId: String) {
        if (lastFetchedClientId == clientId && _state.value.client.id == clientId) return

        val roomResult = repository.getClientByIdRoom(clientId)
        roomResult.fold(
            onSuccess = { client ->
                updateClientState(client)
            },
            onFailure = {
                val supabaseResult = repository.getClientByIdSupabase(clientId)
                supabaseResult.fold(
                    onSuccess = { client ->
                        updateClientState(client)
                    },
                    onFailure = {
                        _eventChannel.send(ShowErrorSnackbar("No se pudo recuperar la información del cliente"))
                    }
                )
            }
        )
    }

    private fun updateClientState(client: ClientModel) {
        lastFetchedClientId = client.id
        _state.update { currentState ->
            currentState.copy(client = client)
        }
    }

    private fun deletePatient() {
        val cs = _state.value
        _state.update { it.copy(isLoadingDeletePatient = true) }
        viewModelScope.launch {
            repository.deletePatient(
                patientId = cs.patientWithCatalogs.patient.id,
                comment = cs.deleteComment
            ).fold(
                onSuccess = {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoadingDeletePatient = false,
                            isDeleteSuccess = true, // Notificamos éxito para que la UI anime el cierre
                            deleteComment = ""
                        )
                    }
                    _eventChannel.send(
                        ShowSuccessSnackbar(message = "Paciente eliminado exitosamente")
                    )
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDeletePatient = false, isDeleteSuccess = true) }
                    _eventChannel.send(
                        ShowErrorSnackbar(message = "No se pudo eliminar el paciente")
                    )
                }
            )
        }
    }

    private fun restorePatient() {
        val cs = _state.value
        _state.update { it.copy(isLoadingRestorePatient = true) }
        viewModelScope.launch {
            repository.changeStatusPatient(
                patientId = cs.patientWithCatalogs.patient.id,
                newStatus = Constants.ACTIVE_PATIENT_STATUS
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoadingRestorePatient = false,
                            showDialogConfirmRestore = false,
                        )
                    }
                    _eventChannel.send(
                        ShowSuccessSnackbar(message = "Paciente activado exitosamente")
                    )
                },
                onFailure = {
                    _state.update {
                        it.copy(isLoadingRestorePatient = false, showDialogConfirmRestore = false)
                    }
                    _eventChannel.send(
                        ShowErrorSnackbar(message = "No se pudo activar al paciente")
                    )
                }
            )
        }
    }
}