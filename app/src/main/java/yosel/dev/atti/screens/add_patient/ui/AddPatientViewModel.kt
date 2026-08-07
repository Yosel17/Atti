package yosel.dev.atti.screens.add_patient.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import yosel.dev.atti.screens.add_patient.domain.AddPatientRepository
import javax.inject.Inject

@HiltViewModel
class AddPatientViewModel @Inject constructor(
    private val repository: AddPatientRepository
): ViewModel() {

    private val _state = MutableStateFlow(AddPatientState())
    val state: StateFlow<AddPatientState> = _state

    fun onAction(action: AddPatientAction){
        when(action){
            else -> {}
        }
    }
}