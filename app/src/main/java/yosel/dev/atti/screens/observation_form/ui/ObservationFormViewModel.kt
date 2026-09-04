package yosel.dev.atti.screens.observation_form.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.models.model.ObservationModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.observation_form.domain.ObservationFormRepository

@HiltViewModel(assistedFactory = ObservationFormViewModel.Factory::class)
class ObservationFormViewModel @AssistedInject constructor(
    private val repository: ObservationFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("observationId") private val observationId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("observationId") observationId: String?
        ): ObservationFormViewModel
    }

    private val _state = MutableStateFlow(
        ObservationFormState(
            isEditMode = !observationId.isNullOrBlank(),
            observationId = observationId
        )
    )
    val state: StateFlow<ObservationFormState> = _state

    private val _eventChannel = Channel<ObservationFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadInitialData()
    }

    fun onAction(action: ObservationFormAction) {
        when (action) {
            ObservationFormAction.TryLoadAgain -> loadInitialData()
            ObservationFormAction.SaveObservation -> saveObservation()
            is ObservationFormAction.ToggleSaveDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }
            is ObservationFormAction.OnObservationChange -> {
                _state.update {
                    it.copy(
                        formInputState = it.formInputState.copy(observation = action.text)
                    )
                }
            }
        }
    }

    private fun loadInitialData() {
        _state.update { it.copy(isLoadingDataInitial = true) }
        viewModelScope.launch {
            repository.getConsultation(consultationId.orEmpty()).fold(
                onSuccess = { consultation ->
                    _state.update {
                        it.copy(
                            consultationWithDetails = consultation,
                            isSuccessGetData = true
                        )
                    }
                    loadExistingObservation()
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(ObservationFormEvent.ShowErrorSnackbar("No se pudo cargar la información de la consulta."))
                }
            )
        }
    }

    private fun loadExistingObservation() {
        viewModelScope.launch {
            repository.getObservationByConsultationId(consultationId.orEmpty()).fold(
                onSuccess = { existing ->
                    if (existing != null) {
                        val formState = ObservationFormInputsState(observation = existing.observation)
                        _state.update {
                            it.copy(
                                isEditMode = true,
                                observationId = existing.id,
                                existingObservation = existing,
                                formInputState = formState,
                                initialFormInputState = formState,
                                isLoadingDataInitial = false
                            )
                        }
                    } else {
                        _state.update { it.copy(isLoadingDataInitial = false) }
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(ObservationFormEvent.ShowErrorSnackbar("No se pudo obtener la observación previa."))
                }
            )
        }
    }

    private fun saveObservation() {
        val s = _state.value
        if (!s.formInputState.isValid) return
        if (s.isEditMode) {
            updateExistingObservation()
        } else {
            registerNewObservation()
        }
    }

    private fun registerNewObservation() {
        val s = _state.value
        _state.update { it.copy(isLoadingSaveObservation = true) }
        viewModelScope.launch {
            val observation = ObservationModel(
                consultationId = consultationId.orEmpty(),
                observation = s.formInputState.observation.trim(),
                status = Constants.ACTIVE_STATUS
            )
            repository.saveObservation(observation).fold(
                onSuccess = { savedObservation ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            observationId = savedObservation.id,
                            existingObservation = savedObservation,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSaveObservation = false
                        )
                    }
                    _eventChannel.send(ObservationFormEvent.ShowSuccessSnackbar("Observaciones guardadas exitosamente."))
                },
                onFailure = { error ->
                    Log.e("ObservationFormVM", "Error al guardar observación", error)
                    _state.update { it.copy(isLoadingSaveObservation = false) }
                    _eventChannel.send(ObservationFormEvent.ShowErrorSnackbar("No se pudo guardar la observación."))
                }
            )
        }
    }

    private fun updateExistingObservation() {
        val s = _state.value
        val existingId = s.observationId ?: s.existingObservation?.id ?: return
        _state.update { it.copy(isLoadingUpdateObservation = true) }
        viewModelScope.launch {
            val observation = ObservationModel(
                id = existingId,
                consultationId = consultationId.orEmpty(),
                observation = s.formInputState.observation.trim(),
                createdAt = s.existingObservation?.createdAt.orEmpty(),
                status = Constants.ACTIVE_STATUS
            )
            repository.updateObservation(observation).fold(
                onSuccess = { updatedObservation ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingObservation = updatedObservation,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingUpdateObservation = false
                        )
                    }
                    _eventChannel.send(ObservationFormEvent.ShowSuccessSnackbar("Observaciones actualizadas correctamente."))
                },
                onFailure = { error ->
                    Log.e("ObservationFormVM", "Error al actualizar observación", error)
                    _state.update { it.copy(isLoadingUpdateObservation = false) }
                    _eventChannel.send(ObservationFormEvent.ShowErrorSnackbar("No se pudo actualizar la observación."))
                }
            )
        }
    }
}