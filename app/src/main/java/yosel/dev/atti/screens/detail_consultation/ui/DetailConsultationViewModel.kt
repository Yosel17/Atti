package yosel.dev.atti.screens.detail_consultation.ui

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
import yosel.dev.atti.screens.detail_consultation.domain.DetailConsultationRepository

@HiltViewModel(assistedFactory = DetailConsultationViewModel.Factory::class)
class DetailConsultationViewModel @AssistedInject constructor(
    private val repository: DetailConsultationRepository,
    @Assisted private val consultationId: String
): ViewModel() {

    @AssistedFactory
    interface Factory{
        fun create(consultationId: String): DetailConsultationViewModel
    }

    private val _state = MutableStateFlow(DetailConsultationState())
    val state: StateFlow<DetailConsultationState> = _state

    private val _eventChannel = Channel<DetailConsultationEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        getConsultationSteps()
    }

    private fun getConsultationSteps() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.getConsultationSteps().fold(
                onSuccess = { consultationSteps ->
                    _state.update { it.copy(consultationSteps = consultationSteps) }
                    observeConsultationWithDetails()
                },
                onFailure = {
                    _state.update { it.copy(isLoading = false) }
                    _eventChannel.send(
                        DetailConsultationEvent.ShowErrorSnackbar(
                            message = "No pudimos cargar los pasos de la consulta."
                        )
                    )
                }
            )
        }
    }

    private suspend fun observeConsultationWithDetails() {
        repository.getConsultationWithDetailsFlow(consultationId = consultationId)
            .catch {
                _state.update { it.copy(isLoading = false) }
                _eventChannel.send(
                    DetailConsultationEvent.ShowErrorSnackbar(
                        message = "No pudimos cargar la información de la consulta."
                    )
                )
            }
            .collectLatest { consultationWithDetailsModel ->
                _state.update {
                    it.copy(consultationWithDetails = consultationWithDetailsModel ?: it.consultationWithDetails,
                        isLoading = false
                    )
                }
            }
    }
}