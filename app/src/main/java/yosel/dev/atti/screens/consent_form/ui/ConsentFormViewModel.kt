package yosel.dev.atti.screens.consent_form.ui

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
import yosel.dev.atti.screens.consent_form.domain.ConsentFormRepository

@HiltViewModel(assistedFactory = ConsentFormViewModel.Factory::class)
class ConsentFormViewModel @AssistedInject constructor(
    private val repository: ConsentFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("consentId") private val consentId: String?
): ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("consentId") consentId: String?
        ): ConsentFormViewModel
    }

    private val _state = MutableStateFlow(
        ConsentFormState(
            isEditMode = !consentId.isNullOrBlank(),
            consentId = consentId
        )
    )
    val state: StateFlow<ConsentFormState> = _state

    private val _eventChannel = Channel<ConsentFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadInitialData()
    }

    fun onAction(action: ConsentFormAction){
        when(action){
            ConsentFormAction.OnDismissBottomSheet -> {
                _state.update { it.copy(isBottomSheetVisible = false) }
            }
            is ConsentFormAction.OnImageSelected -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(imageUri = action.uri)) }
            }
            ConsentFormAction.OnObtainPermits -> {
                _state.update { it.copy(isBottomSheetVisible = false) }
                viewModelScope.launch {
                    _eventChannel.send(ConsentFormEvent.LaunchPermission)
                }
            }
            ConsentFormAction.OnSelectCameraClick -> {
                _state.update { it.copy(isBottomSheetVisible = false) }
                viewModelScope.launch {
                    _eventChannel.send(ConsentFormEvent.LaunchCamera)
                }
            }
            ConsentFormAction.OnSelectGalleryClick -> {
                _state.update { it.copy(isBottomSheetVisible = false) }
                viewModelScope.launch {
                    _eventChannel.send(ConsentFormEvent.LaunchGallery)
                }
            }
            is ConsentFormAction.OnToggleRationaleDialog -> {
                _state.update { it.copy(showRationaleDialog = action.show) }
            }
            is ConsentFormAction.OnToggleSettingsDialog -> {
                _state.update { it.copy(showSettingsDialog = action.show) }
            }
            ConsentFormAction.SaveConsent -> saveConsent()
            is ConsentFormAction.ToggleSaveDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }
            ConsentFormAction.TryLoadAgain -> loadInitialData()
            ConsentFormAction.OnUploadImageClick -> {
                _state.update { it.copy(isBottomSheetVisible = true) }
            }
        }
    }

    private fun loadInitialData(){
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
                    if (_state.value.isEditMode){
                        loadExistingConsent()
                    }else{
                        _state.update { it.copy(isLoadingDataInitial = false) }
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(ConsentFormEvent.ShowErrorSnackbar("No se pudo cargar la información de la consulta."))
                }
            )
        }
    }

    private fun loadExistingConsent(){
        viewModelScope.launch {
            repository.getConsentByConsultationId(consultationId = consultationId.orEmpty()).fold(
                onSuccess = { existing ->
                    if (existing != null){
                        _state.update {
                            it.copy(
                                isEditMode = true,
                                consentId = existing.id,
                                existingConsent = existing,
                                isLoadingDataInitial = false
                            )
                        }
                    }else{
                        _state.update { it.copy(isLoadingDataInitial = false) }
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(ConsentFormEvent.ShowErrorSnackbar("No se pudo obtener el consentimiento previo."))
                }
            )
        }
    }

    private fun saveConsent(){

    }
}