package yosel.dev.atti.screens.detail_service.ui

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
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.detail_service.domain.DetailServiceRepository

@HiltViewModel(assistedFactory = DetailServiceViewModel.Factory::class)
class DetailServiceViewModel @AssistedInject constructor(
    private val repository: DetailServiceRepository,
    @Assisted private val serviceId: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(serviceId: String): DetailServiceViewModel
    }

    private val _state = MutableStateFlow(DetailServiceState())
    val state: StateFlow<DetailServiceState> = _state

    private val _eventChannel = Channel<DetailServiceEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        observeService()
    }

    private fun observeService() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getServiceWithDetailsByIdFlow(serviceId)
                .catch {
                    _state.update { currentState -> currentState.copy(isLoading = false) }
                    _eventChannel.send(
                        DetailServiceEvent.ShowErrorSnackbar(
                            message = "No pudimos cargar la información del servicio."
                        )
                    )
                }
                .collectLatest { serviceWithDetailsModel ->
                    _state.update { currentState ->
                        currentState.copy(
                            serviceWithDetails = serviceWithDetailsModel ?: currentState.serviceWithDetails,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onAction(action: DetailServiceAction) {
        when (action) {
            DetailServiceAction.OnEditClick -> {
                val currentService = _state.value.serviceWithDetails.service
                if (currentService.status == Constants.DELETED_STATUS) {
                    _state.update { it.copy(showDialogInformation = true) }
                } else {
                    viewModelScope.launch {
                        _eventChannel.send(
                            DetailServiceEvent.OnNavigationMain(
                                Screens.ServiceForm(serviceId = currentService.id)
                            )
                        )
                    }
                }
            }
            is DetailServiceAction.ToggleShowDialogConfirmDelete -> {
                _state.update { it.copy(showDialogConfirmDelete = action.show) }
            }
            DetailServiceAction.DeleteService -> deleteService()
            is DetailServiceAction.ToggleShowDialogConfirmRestore -> {
                _state.update { it.copy(showDialogConfirmRestore = action.show) }
            }
            DetailServiceAction.RestoreService -> restoreService()
            is DetailServiceAction.ToggleShowDialogInformation -> {
                _state.update { it.copy(showDialogInformation = action.show) }
            }
            is DetailServiceAction.OnNavigationMain -> {
                viewModelScope.launch {
                    _eventChannel.send(DetailServiceEvent.OnNavigationMain(action.screen))
                }
            }
        }
    }

    private fun deleteService() {
        val currentService = _state.value.serviceWithDetails.service
        _state.update { it.copy(isLoadingDeleteService = true) }
        viewModelScope.launch {
            repository.changeStatusService(
                serviceId = currentService.id,
                newStatus = Constants.DELETED_STATUS
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoadingDeleteService = false,
                            showDialogConfirmDelete = false
                        )
                    }
                    _eventChannel.send(
                        DetailServiceEvent.ShowSuccessSnackbar("Servicio eliminado exitosamente")
                    )
                },
                onFailure = {
                    _state.update {
                        it.copy(isLoadingDeleteService = false, showDialogConfirmDelete = false)
                    }
                    _eventChannel.send(
                        DetailServiceEvent.ShowErrorSnackbar("No se pudo eliminar el servicio")
                    )
                }
            )
        }
    }

    private fun restoreService() {
        val currentService = _state.value.serviceWithDetails.service
        _state.update { it.copy(isLoadingRestoreService = true) }
        viewModelScope.launch {
            repository.changeStatusService(
                serviceId = currentService.id,
                newStatus = Constants.ACTIVE_STATUS
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoadingRestoreService = false,
                            showDialogConfirmRestore = false
                        )
                    }
                    _eventChannel.send(
                        DetailServiceEvent.ShowSuccessSnackbar("Servicio restaurado exitosamente")
                    )
                },
                onFailure = {
                    _state.update {
                        it.copy(isLoadingRestoreService = false, showDialogConfirmRestore = false)
                    }
                    _eventChannel.send(
                        DetailServiceEvent.ShowErrorSnackbar("No se pudo restaurar el servicio")
                    )
                }
            )
        }
    }
}