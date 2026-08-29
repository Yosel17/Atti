package yosel.dev.atti.screens.detail_consultation.ui

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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.detail_consultation.domain.DetailConsultationRepository

@HiltViewModel(assistedFactory = DetailConsultationViewModel.Factory::class)
class DetailConsultationViewModel @AssistedInject constructor(
    private val repository: DetailConsultationRepository,
    @Assisted private val consultationId: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(consultationId: String): DetailConsultationViewModel
    }

    private val _state = MutableStateFlow(DetailConsultationState())
    val state: StateFlow<DetailConsultationState> = _state

    private val _eventChannel = Channel<DetailConsultationEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        observeConsultation()
        observeStepsProgress()
        syncData()
    }

    private fun observeConsultation() {
        viewModelScope.launch {
            repository.getConsultationWithDetailsFlow(consultationId = consultationId)
                .catch {
                    _eventChannel.send(
                        DetailConsultationEvent.ShowErrorSnackbar("No pudimos cargar la información de la consulta.")
                    )
                }
                .collectLatest { consultationModel ->
                    _state.update {
                        it.copy(
                            consultationWithDetails = consultationModel ?: it.consultationWithDetails,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun observeStepsProgress() {
        viewModelScope.launch {
            repository.getConsultationStepsProgressFlow(
                consultationId = consultationId,
                consultationTypeId = Constants.GENERAL_CONSULTATION_TYPE
            ).catch {
                _eventChannel.send(
                    DetailConsultationEvent.ShowErrorSnackbar("No pudimos cargar los pasos de la consulta.")
                )
            }.collectLatest { steps ->
                _state.update { it.copy(consultationSteps = steps) }
            }
        }
    }

    private fun syncData() {
        viewModelScope.launch {
            repository.syncConsultationSteps(
                consultationId = consultationId,
                consultationTypeId = Constants.GENERAL_CONSULTATION_TYPE
            ).onFailure {
                Log.e("DetailConsultationViewModel", "Error al sincronizar datos", it)
                _eventChannel.send(
                    DetailConsultationEvent.ShowErrorSnackbar("Error al sincronizar el progreso de la consulta.")
                )
            }
        }
    }
}