package yosel.dev.atti.screens.physio_consts_form.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.physio_consts_form.domain.PhysioConstsFormRepository
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = PhysioConstsFormViewModel.Factory::class)
class PhysioConstsFormViewModel @AssistedInject constructor(
    private val repository: PhysioConstsFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("constsId") private val constsId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("constsId") constsId: String?
        ): PhysioConstsFormViewModel
    }

    private val _state = MutableStateFlow(
        PhysioConstsFormState(
            isEditMode = !constsId.isNullOrBlank(),
            constsId = constsId
        )
    )
    val state: StateFlow<PhysioConstsFormState> = _state

    private val _eventChannel = Channel<PhysioConstsFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var filterJob: Job? = null

    init {
        getConsultation()
    }

    fun onAction(action: PhysioConstsFormAction) {
        when (action) {
            PhysioConstsFormAction.TryCatalogsAgain -> getConsultation()
            PhysioConstsFormAction.SaveConstants -> saveConstants()
            is PhysioConstsFormAction.ToggleSaveDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }
            is PhysioConstsFormAction.OnTemperatureChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(temperature = action.value)) }
            }
            is PhysioConstsFormAction.OnHeartRateChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(heartRate = action.value)) }
            }
            is PhysioConstsFormAction.OnRespiratoryRateChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(respiratoryRate = action.value)) }
            }
            is PhysioConstsFormAction.OnWeightChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(weight = action.value)) }
            }
            is PhysioConstsFormAction.OnCapillaryRefillTimeChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(capillaryRefillTime = action.value)) }
            }
            is PhysioConstsFormAction.OnSkinTurgorChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(skinTurgor = action.value)) }
            }
            PhysioConstsFormAction.OnOpenWeightUnitSheet -> {
                _state.update { it.copy(isWeightUnitSheetOpen = true, weightUnitSearchQuery = "", filteredWeightUnits = it.weightUnits) }
            }
            PhysioConstsFormAction.OnDismissWeightUnitSheet -> {
                _state.update { it.copy(isWeightUnitSheetOpen = false) }
            }
            is PhysioConstsFormAction.OnSearchWeightUnitQueryChange -> {
                _state.update { it.copy(weightUnitSearchQuery = action.query) }
                debounceSearch {
                    val q = action.query.normalize()
                    _state.update { s ->
                        s.copy(filteredWeightUnits = if (q.isBlank()) s.weightUnits else s.weightUnits.filter { it.name.normalize().contains(q) })
                    }
                }
            }
            is PhysioConstsFormAction.OnSelectWeightUnit -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(selectedWeightUnit = action.unit)) }
            }
            is PhysioConstsFormAction.OnShowAddCatalogDialog -> {
                _state.update {
                    it.copy(
                        activeCatalogTypeId = action.catalogTypeId,
                        activeCatalogTypeName = action.catalogTypeName,
                        showAddAppCatalogDialog = true
                    )
                }
            }
            PhysioConstsFormAction.OnDismissAddCatalogDialog -> {
                _state.update {
                    it.copy(
                        showAddAppCatalogDialog = false,
                        activeCatalogTypeId = 0,
                        activeCatalogTypeName = ""
                    )
                }
            }
            is PhysioConstsFormAction.OnSaveAppCatalog -> onSaveAppCatalog(action.name)
        }
    }

    private fun debounceSearch(block: () -> Unit) {
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            delay(300.milliseconds)
            block()
        }
    }

    private fun getConsultation() {
        _state.update { it.copy(isLoadingDataInitial = true) }
        viewModelScope.launch {
            repository.getConsultation(consultationId = consultationId ?: "").fold(
                onSuccess = { consultationWithDetails ->
                    _state.update { it.copy(consultationWithDetails = consultationWithDetails) }
                    getCatalogs()
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(PhysioConstsFormEvent.ShowErrorSnackbar("No pudimos obtener la información de la consulta."))
                }
            )
        }
    }

    private fun getCatalogs() {
        viewModelScope.launch {
            repository.getAppCatalogsByTypes(
                types = listOf(Constants.UNIT_OF_WEIGHT_TYPE_CATALOG)
            ).fold(
                onSuccess = { appCatalogs ->
                    successGetCatalogs(appCatalogs)
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(PhysioConstsFormEvent.ShowErrorSnackbar("No pudimos obtener los catálogos. Inténtalo de nuevo."))
                }
            )
        }
    }

    private fun successGetCatalogs(appCatalogs: List<AppCatalogModel>) {
        val weightUnits = appCatalogs.sortedBy { it.name.lowercase() }
        _state.update { currentState ->
            currentState.copy(
                weightUnits = weightUnits,
                filteredWeightUnits = weightUnits,
                isSuccessGetCatalogs = true
            )
        }

        if (!constsId.isNullOrBlank()) {
            loadConstantsForEdit(id = constsId, catalogs = weightUnits)
        } else {
            // Inicializar valores por defecto acordes a la especie
            val speciesId = _state.value.consultationWithDetails.patientWithDetails.patient.speciesId
            val defaultKgUnit = weightUnits.firstOrNull()

            val defaultTemp = when (speciesId) {
                Constants.CANINE_SPECIES_CATALOG -> "37.6"
                Constants.FELINE_SPECIES_CATALOG -> "38.2"
                else -> ""
            }
            val defaultHeartRate = when (speciesId) {
                Constants.CANINE_SPECIES_CATALOG -> "60"
                Constants.FELINE_SPECIES_CATALOG -> "140"
                else -> ""
            }
            val defaultRespRate = when (speciesId) {
                Constants.CANINE_SPECIES_CATALOG -> "10"
                Constants.FELINE_SPECIES_CATALOG -> "24"
                else -> ""
            }

            val defaultForm = PhysioConstsFormInputsState(
                temperature = defaultTemp,
                heartRate = defaultHeartRate,
                respiratoryRate = defaultRespRate,
                selectedWeightUnit = defaultKgUnit,
                capillaryRefillTime = 2,
                skinTurgor = 1
            )

            _state.update {
                it.copy(
                    formInputState = defaultForm,
                    initialFormInputState = defaultForm,
                    isLoadingDataInitial = false
                )
            }
        }
    }

    private fun loadConstantsForEdit(id: String, catalogs: List<AppCatalogModel>) {
        viewModelScope.launch {
            repository.getPhysiologicalConstsWithDetailsById(id).fold(
                onSuccess = { constsWithDetails ->
                    val weightUnit = catalogs.find { it.id == constsWithDetails.constants.weightUnitCatalogId }
                    val initialForm = PhysioConstsFormInputsState(
                        temperature = constsWithDetails.constants.temperature?.toString() ?: "",
                        heartRate = constsWithDetails.constants.heartRate?.toString() ?: "",
                        respiratoryRate = constsWithDetails.constants.respiratoryRate?.toString() ?: "",
                        weight = constsWithDetails.constants.weight?.toString() ?: "",
                        selectedWeightUnit = weightUnit ?: constsWithDetails.weightUnit.takeIf { it.id != 0 },
                        capillaryRefillTime = constsWithDetails.constants.capillaryRefillTime ?: 2,
                        skinTurgor = constsWithDetails.constants.skinTurgor ?: 1
                    )
                    _state.update { currentState ->
                        currentState.copy(
                            currentConstants = constsWithDetails.constants,
                            formInputState = initialForm,
                            initialFormInputState = initialForm,
                            isLoadingDataInitial = false
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(PhysioConstsFormEvent.ShowErrorSnackbar("Error al cargar las constantes fisiológicas."))
                }
            )
        }
    }

    private fun saveConstants() {
        if (_state.value.isEditMode) {
            updateConstants()
        } else {
            registerConstants()
        }
    }

    private fun registerConstants() {
        val currentState = _state.value
        _state.update { it.copy(isLoadingSave = true) }
        viewModelScope.launch {
            val model = currentState.formInputState.toModel(consultationId = consultationId ?: "")
            repository.savePhysiologicalConsts(constants = model).fold(
                onSuccess = { saved ->
                    val currentForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isEditMode = true,
                            constsId = saved.id,
                            currentConstants = saved,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSave = false
                        )
                    }
                    _eventChannel.send(PhysioConstsFormEvent.ShowSuccessSnackbar("Constantes fisiológicas guardadas exitosamente."))
                },
                onFailure = {
                    Log.e("PhysiolConstsViewModel", "Error al guardar constantes", it)
                    _state.update { it.copy(isLoadingSave = false) }
                    _eventChannel.send(PhysioConstsFormEvent.ShowErrorSnackbar("No pudimos guardar las constantes. Inténtalo de nuevo."))
                }
            )
        }
    }

    private fun updateConstants() {
        val currentState = _state.value
        val current = currentState.currentConstants ?: return
        _state.update { it.copy(isLoadingUpdate = true) }
        viewModelScope.launch {
            val model = currentState.formInputState.toUpdateModel(
                constsId = current.id,
                consultationId = current.consultationId,
                createdAt = current.createdAt,
                status = current.status
            )
            repository.updatePhysiologicalConsts(constants = model).fold(
                onSuccess = { updated ->
                    val newForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isLoadingUpdate = false,
                            currentConstants = updated,
                            formInputState = newForm,
                            initialFormInputState = newForm
                        )
                    }
                    _eventChannel.send(PhysioConstsFormEvent.ShowSuccessSnackbar("Constantes fisiológicas actualizadas correctamente."))
                },
                onFailure = {
                    Log.e("PhysiolConstsViewModel", "Error al actualizar constantes", it)
                    _state.update { it.copy(isLoadingUpdate = false) }
                    _eventChannel.send(PhysioConstsFormEvent.ShowErrorSnackbar("No pudimos actualizar las constantes. Inténtalo de nuevo."))
                }
            )
        }
    }

    private fun onSaveAppCatalog(name: String) {
        val currentState = _state.value
        _state.update { it.copy(isLoadingAddCatalog = true) }
        viewModelScope.launch {
            val newCatalog = AppCatalogModel(
                id = 0,
                catalogTypeId = currentState.activeCatalogTypeId,
                name = name,
                description = "",
                isActive = true,
                createdAt = ""
            )
            repository.insertCatalog(catalog = newCatalog).fold(
                onSuccess = { inserted ->
                    _state.update { state ->
                        val updatedUnits = (state.weightUnits + inserted).sortedBy { it.name.lowercase() }
                        state.copy(
                            weightUnits = updatedUnits,
                            filteredWeightUnits = updatedUnits,
                            formInputState = state.formInputState.copy(selectedWeightUnit = inserted),
                            isLoadingAddCatalog = false,
                            showAddAppCatalogDialog = false
                        )
                    }
                    _eventChannel.send(PhysioConstsFormEvent.ShowToast("${currentState.activeCatalogTypeName} agregado correctamente."))
                },
                onFailure = {
                    _state.update { it.copy(isLoadingAddCatalog = false, showAddAppCatalogDialog = false) }
                    _eventChannel.send(PhysioConstsFormEvent.ShowToast("No se pudo agregar el catálogo."))
                }
            )
        }
    }
}