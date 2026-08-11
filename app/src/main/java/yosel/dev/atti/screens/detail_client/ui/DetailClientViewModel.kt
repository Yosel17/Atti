package yosel.dev.atti.screens.detail_client.ui

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
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.toEditFormState
import yosel.dev.atti.core.utils.toModel
import yosel.dev.atti.screens.detail_client.domain.DetailClientRepository
import yosel.dev.atti.screens.detail_client.ui.DetailClientEvent.OnCallClick
import yosel.dev.atti.screens.detail_client.ui.DetailClientEvent.OnWhatsappClick

@HiltViewModel(assistedFactory = DetailClientViewModel.Factory::class)
class DetailClientViewModel @AssistedInject constructor(
    private val repository: DetailClientRepository,
    @Assisted private val clienteId: String,
    @Assisted private val isLocalPatients: Boolean,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(clienteId: String, isLocalPatients: Boolean): DetailClientViewModel
    }

    private val _state = MutableStateFlow(DetailClientState())
    val state: StateFlow<DetailClientState> = _state

    private val _eventChannel = Channel<DetailClientEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        observeClientWithPatients()
        syncPatientsIfNeeded()
    }

    private fun observeClientWithPatients() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getClientWithPatientsFlow(clienteId)
                .catch {
                    _state.update { currentState -> currentState.copy(isLoading = false) }
                    _eventChannel.send(
                        DetailClientEvent.ShowErrorSnackbar(
                            message = "No pudimos cargar la información del cliente."
                        )
                    )
                }
                .collectLatest { clientWithPatientsModel ->
                    _state.update { currentState ->
                        currentState.copy(
                            clientWithPatients = clientWithPatientsModel ?: currentState.clientWithPatients,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun syncPatientsIfNeeded() {
        viewModelScope.launch {
            repository.syncPatientsIfNeeded(clientId = clienteId, isLocalPatients = isLocalPatients)
                .onFailure {
                    _eventChannel.send(
                        DetailClientEvent.ShowErrorSnackbar(
                            message = "No pudimos sincronizar las mascotas del cliente."
                        )
                    )
                }
        }
    }

    fun onAction(action: DetailClientAction) {
        when (action) {
            is DetailClientAction.OnCallClick -> {
                viewModelScope.launch {
                    _eventChannel.send(
                        OnCallClick(phoneNumber = action.phoneNumber)
                    )
                }
            }
            is DetailClientAction.OnWhatsappClick -> {
                viewModelScope.launch {
                    _eventChannel.send(
                        OnWhatsappClick(phoneNumber = action.phoneNumber)
                    )
                }
            }
            DetailClientAction.OnEditClick -> {
                _state.update {
                    val editForm = it.clientWithPatients.client.toEditFormState()
                    it.copy(
                        isEditing = true,
                        editFormState = editForm,
                        initialEditFormState = editForm
                    )
                }
            }
            DetailClientAction.OnDismissEdit -> {
                _state.update { it.copy(isEditing = false) }
            }
            is DetailClientAction.OnChangeEditFormValue -> {
                onValueEditFormChange(action.value, action.field)
            }
            DetailClientAction.OnUpdateClient -> updateClient()
            DetailClientAction.DeleteClient -> deletePatients()
            DetailClientAction.RestoreClient -> restorePatients()
            is DetailClientAction.ToggleShowDialogConfirmDelete -> {
                _state.update { it.copy(showDialogConfirmDelete = action.show) }
            }
            is DetailClientAction.ToggleShowDialogConfirmRestore -> {
                _state.update { it.copy(showDialogConfirmRestore = action.show) }
            }
            is DetailClientAction.ToggleShowDialogInformation -> {
                _state.update { it.copy(showDialogInformation = action.show) }
            }

            is DetailClientAction.OnNavigationMain -> {
                viewModelScope.launch {
                    _eventChannel.send(DetailClientEvent.OnNavigationMain(screen = action.screen))
                }
            }
        }
    }

    private fun onValueEditFormChange(value: String, field: Int) {
        _state.update {
            val newFormState = when (field) {
                Constants.FIRST_NAME_FIELD -> it.editFormState.copy(firstName = value)
                Constants.LAST_NAME_FIELD -> it.editFormState.copy(lastName = value)
                Constants.DOCUMENT_ID_FIELD -> it.editFormState.copy(documentId = value)
                Constants.PHONE_NUMBER_FIELD -> it.editFormState.copy(phoneNumber = value)
                Constants.EMAIL_FIELD -> it.editFormState.copy(email = value)
                Constants.ADDRESS_FIELD -> it.editFormState.copy(address = value)
                else -> it.editFormState
            }
            it.copy(editFormState = newFormState.copy(touchedFields = newFormState.touchedFields + field))
        }
    }

    private fun updateClient() {
        val currentState = _state.value
        if (!currentState.editFormState.isValid) {
            _state.update {
                it.copy(
                    editFormState = it.editFormState.copy(
                        touchedFields = setOf(
                            Constants.FIRST_NAME_FIELD,
                            Constants.LAST_NAME_FIELD,
                            Constants.DOCUMENT_ID_FIELD,
                            Constants.PHONE_NUMBER_FIELD,
                            Constants.ADDRESS_FIELD
                        )
                    )
                )
            }
            return
        }
        _state.update { it.copy(isLoadingUpdate = true) }
        val updatedClient = currentState.editFormState.toModel(
            status = currentState.clientWithPatients.client.status
        )
        viewModelScope.launch {
            repository.updateClient(client = updatedClient)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoadingUpdate = false,
                            isEditing = false
                        )
                    }
                    _eventChannel.send(DetailClientEvent.ShowSuccessSnackbar("Información actualizada correctamente."))
                }
                .onFailure {
                    _state.update { it.copy(isLoadingUpdate = false) }
                    _eventChannel.send(DetailClientEvent.ShowErrorSnackbar("No se pudo actualizar la información. Inténtalo de nuevo."))
                }
        }
    }

    private fun deletePatients() {
        val cs = _state.value
        _state.update {
            it.copy(isLoadingDeleteClient = true)
        }
        val patientsIds = cs.clientWithPatients.patients.map { it.id }
        viewModelScope.launch {
            repository.updatePatientsStatus(
                patientIds = patientsIds, newStatus = Constants.DELETED_PATIENT_STATUS
            ).fold(
                onSuccess = {
                    deleteClient(clientId = cs.clientWithPatients.client.id)
                },
                onFailure = {
                    _state.update {
                        it.copy(isLoadingDeleteClient = false, showDialogConfirmDelete = false)
                    }
                    _eventChannel.send(
                        DetailClientEvent.ShowErrorSnackbar(message = "No se pudo eliminar a los pacientes del cliente")
                    )
                }
            )
        }
    }

    private suspend fun deleteClient(clientId: String) {
        repository.updateClientStatus(
            clientId = clientId, newStatus = Constants.DELETED_CLIENT_STATUS
        ).fold(
            onSuccess = {
                _state.update {
                    it.copy(
                        isLoadingDeleteClient = false,
                        showDialogConfirmDelete = false
                    )
                }
                _eventChannel.send(
                    DetailClientEvent.ShowSuccessSnackbar(message = "Cliente eliminado exitosamente")
                )
            },
            onFailure = {
                _state.update {
                    it.copy(isLoadingDeleteClient = false, showDialogConfirmDelete = false)
                }
                _eventChannel.send(
                    DetailClientEvent.ShowErrorSnackbar(message = "No se pudo eliminar al cliente")
                )
            }
        )
    }

    private fun restorePatients() {
        val cs = _state.value
        _state.update {
            it.copy(isLoadingRestoreClient = true)
        }
        val patientsIds = cs.clientWithPatients.patients.map { it.id }
        viewModelScope.launch {
            repository.updatePatientsStatus(
                patientIds = patientsIds, newStatus = Constants.ACTIVE_PATIENT_STATUS
            ).fold(
                onSuccess = {
                    restoreClient(clientId = cs.clientWithPatients.client.id)
                },
                onFailure = {
                    _state.update {
                        it.copy(isLoadingRestoreClient = false, showDialogConfirmRestore = false)
                    }
                    _eventChannel.send(
                        DetailClientEvent.ShowErrorSnackbar(message = "No se pudo restaurar a los pacientes del cliente")
                    )
                }
            )
        }
    }

    private suspend fun restoreClient(clientId: String) {
        repository.updateClientStatus(
            clientId = clientId, newStatus = Constants.ACTIVE_CLIENT_STATUS
        ).fold(
            onSuccess = {
                _state.update {
                    it.copy(
                        isLoadingRestoreClient = false,
                        showDialogConfirmRestore = false
                    )
                }
                _eventChannel.send(
                    DetailClientEvent.ShowSuccessSnackbar(message = "Cliente restaurado exitosamente")
                )
            },
            onFailure = {
                _state.update {
                    it.copy(isLoadingRestoreClient = false, showDialogConfirmRestore = false)
                }
                _eventChannel.send(
                    DetailClientEvent.ShowErrorSnackbar(message = "No se pudo restaurar al cliente")
                )
            }
        )
    }
}