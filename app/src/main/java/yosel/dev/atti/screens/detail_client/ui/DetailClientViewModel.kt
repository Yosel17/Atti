package yosel.dev.atti.screens.detail_client.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import yosel.dev.atti.screens.detail_client.domain.DetailClientRepository
import javax.inject.Inject

@HiltViewModel
class DetailClientViewModel @Inject constructor(
    private val repository: DetailClientRepository
): ViewModel() {

    private val _state = MutableStateFlow(DetailClientState())
    val state: StateFlow<DetailClientState> = _state

    private val _eventChannel = Channel<DetailClientEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun onAction(action: DetailClientAction){
        when(action){
            else -> {}
        }
    }
}