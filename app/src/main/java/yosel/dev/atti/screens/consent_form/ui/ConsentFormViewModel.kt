package yosel.dev.atti.screens.consent_form.ui

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
import yosel.dev.atti.core.models.model.ConsentModel
import yosel.dev.atti.core.utils.Constants
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
                        val inputState = ConsentFormInputsState(imageUrl = existing.imageUrl)
                        _state.update {
                            it.copy(
                                isEditMode = true,
                                consentId = existing.id,
                                existingConsent = existing,
                                formInputState = inputState,
                                initialFormInputState = inputState,
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
        val s = _state.value
        if (!s.formInputState.isValid) return
        if (s.isEditMode){
            updateImageConsent()
        }else{
            registerImageConsent()
        }
    }

    private fun registerImageConsent(){
        val s = _state.value
        val currentUri = s.formInputState.imageUri ?: return
        _state.update { it.copy(isLoadingSaveConsent = true) }
        viewModelScope.launch {
            repository.saveImageConsent(image = currentUri, consultationId = consultationId?:"").fold(
                onSuccess = { url ->
                    registerConsent(url = url)
                },
                onFailure = { error ->
                    Log.e("ConsentFormVM", "error al guardar la imagen del consentimiento", error)
                    _state.update { it.copy(isLoadingSaveConsent = false) }
                    _eventChannel.send(ConsentFormEvent.ShowErrorSnackbar("No se pudo guardar la imagen del consentimiento."))
                }
            )
        }
    }

    private suspend fun registerConsent(url: String){
        val consent = ConsentModel(
            consultationId = consultationId.orEmpty(),
            imageUrl = url,
            status = Constants.ACTIVE_STATUS
        )
        repository.saveConsent(consent = consent).fold(
            onSuccess = { savedConsent ->
                val inputState = ConsentFormInputsState(imageUrl = savedConsent.imageUrl)
                _state.update {
                    it.copy(
                        isEditMode = true,
                        consentId = savedConsent.id,
                        existingConsent = savedConsent,
                        formInputState = inputState,
                        initialFormInputState = inputState,
                        isLoadingSaveConsent = false
                    )
                }
                _eventChannel.send(ConsentFormEvent.ShowSuccessSnackbar("Consentimiento guardado con éxito."))
            },
            onFailure = { error ->
                Log.e("ConsentFormVM", "error al guardar el consentimiento", error)
                _state.update { it.copy(isLoadingSaveConsent = false) }
                _eventChannel.send(ConsentFormEvent.ShowErrorSnackbar("No se pudo guardar el consentimiento."))
            }
        )
    }

    private fun updateImageConsent(){
        val s = _state.value
        val currentUri = s.formInputState.imageUri ?: return
        _state.update { it.copy(isLoadingUpdateConsent = true) }
        viewModelScope.launch {
            repository.updateImageConsent(
                image = currentUri,
                previousImageUrl = s.existingConsent?.imageUrl,
                consultationId = consultationId?:""
            ).fold(
                onSuccess = { url ->
                    updateConsent(imageUrl = url)
                },
                onFailure = { error ->
                    Log.e("ConsentFormVM", "error al actualizar la imagen del consentimiento", error)
                    _state.update { it.copy(isLoadingUpdateConsent = false) }
                    _eventChannel.send(ConsentFormEvent.ShowErrorSnackbar("No se pudo actualizar la imagen del consentimiento."))
                }
            )
        }
    }

    private suspend fun updateConsent(imageUrl: String) {
        val s = _state.value
        val existingId = s.consentId ?: s.existingConsent?.id ?: return
        _state.update { it.copy(isLoadingUpdateConsent = true) }
        val consent = ConsentModel(
            id = existingId,
            consultationId = consultationId.orEmpty(),
            imageUrl = imageUrl,
            createdAt = s.existingConsent?.createdAt.orEmpty(),
            status = Constants.ACTIVE_STATUS
        )
        repository.updateConsent(consent = consent).fold(
            onSuccess = {
                val inputState = ConsentFormInputsState(imageUrl = imageUrl)
                _state.update {
                    it.copy(
                        isEditMode = true,
                        existingConsent = it.existingConsent?.copy(imageUrl = imageUrl),
                        formInputState = inputState,
                        initialFormInputState = inputState,
                        isLoadingUpdateConsent = false
                    )
                }
                _eventChannel.send(ConsentFormEvent.ShowSuccessSnackbar("Consentimiento actualizado con éxito."))
            },
            onFailure = {error ->
                Log.e("ConsentFormVM", "error al actualizar el consentimiento", error)
                _state.update { it.copy(isLoadingUpdateConsent = false) }
                _eventChannel.send(ConsentFormEvent.ShowErrorSnackbar("No se pudo actualizar el consentimiento."))
            }
        )
    }
}