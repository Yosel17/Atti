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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.navigation.main.Screens.*
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.detail_patient.domain.DetailPatientRepository
import yosel.dev.atti.screens.detail_patient.ui.DetailPatientEvent.*

@HiltViewModel(assistedFactory = DetailPatientViewModel.Factory::class)
class DetailPatientViewModel @AssistedInject constructor(
    private val repository: DetailPatientRepository,
    @Assisted private val patientId: String,
): ViewModel() {

    @AssistedFactory
    interface Factory{
        fun create(patientId: String): DetailPatientViewModel
    }

    private val _state = MutableStateFlow(DetailPatientState())
    val state: StateFlow<DetailPatientState> = _state

    private val _eventChannel = Channel<DetailPatientEvent>()
    val events = _eventChannel.receiveAsFlow()

    // Control para evitar llamadas repetidas a la red si el ID del cliente no cambia
    private var lastFetchedClientId: String? = null

    fun onAction(action: DetailPatientAction){
        when(action){
            is DetailPatientAction.ToggleShowDialogConfirmDelete -> {
                _state.update { it.copy(showDialogConfirmDelete = action.show) }
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
        }
    }

    init {
        observePatient()
    }

    private fun observePatient() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.getPatientByIdFlow(patientId).collectLatest { patientResult ->
                patientResult.fold(
                    onSuccess = { patient ->
                        _state.update { currentState ->
                            currentState.copy(
                                patient = patient,
                                isLoading = false
                            )
                        }

                        // Si tenemos un clientId válido, cargamos los datos del cliente
                        if (patient.clientId.isNotBlank()) {
                            fetchClientIfNeeded(patient.clientId)
                        }
                    },
                    onFailure = { throwable ->
                        _state.update { it.copy(isLoading = false) }
                        _eventChannel.send(ShowErrorSnackbar("Error al cargar el paciente"))
                    }
                )
            }
        }
    }

    private suspend fun fetchClientIfNeeded(clientId: String) {
        // Evita re-consultar a la API/DB si el clientId no ha cambiado durante esta sesión de la pantalla
        if (lastFetchedClientId == clientId && _state.value.client.id == clientId) return

        // 1. Intentar obtener de Room
        val roomResult = repository.getClientByIdRoom(clientId)

        roomResult.fold(
            onSuccess = { client ->
                updateClientState(client)
            },
            onFailure = {
                // 2. Si falla Room o no existe, intentar obtener de Supabase
                val supabaseResult = repository.getClientByIdSupabase(clientId)
                supabaseResult.fold(
                    onSuccess = { client ->
                        updateClientState(client)
                    },
                    onFailure = { throwable ->
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

    private fun deletePatient(){
        val cs = _state.value

        _state.update {
            it.copy(isLoadingDeletePatient = true)
        }

        viewModelScope.launch {
            repository.changeStatusPatient(
                patientId = cs.patient.id, newStatus = Constants.DELETED_PATIENT_STATUS
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoadingDeletePatient = false,
                            showDialogConfirmDelete = false,
                            patient = it.patient.copy(status = Constants.DELETED_PATIENT_STATUS)
                        )
                    }
                    _eventChannel.send(
                        ShowSuccessSnackbar(message = "Paciente eliminado exitosamente")
                    )
                },
                onFailure = {
                    _state.update {
                        it.copy(isLoadingDeletePatient = false, showDialogConfirmDelete = false)
                    }
                    _eventChannel.send(
                        ShowErrorSnackbar(message = "No se pudo eliminar el paciente")
                    )
                }
            )
        }
    }

    private fun restorePatient(){
        val cs = _state.value

        _state.update {
            it.copy(isLoadingRestorePatient = true)
        }

        viewModelScope.launch {
            repository.changeStatusPatient(
                patientId = cs.patient.id, newStatus = Constants.ACTIVE_PATIENT_STATUS
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoadingRestorePatient = false,
                            showDialogConfirmRestore = false,
                            patient = it.patient.copy(status = Constants.ACTIVE_PATIENT_STATUS)
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