package yosel.dev.atti.screens.detail_patient.ui

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import yosel.dev.atti.screens.detail_patient.domain.DetailPatientRepository

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

    init {
        getPatientById(patientId = patientId)
    }



}