package yosel.dev.atti.screens.fasting_form.ui

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
import yosel.dev.atti.screens.fasting_form.domain.FastingFormRepository
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = FastingFormViewModel.Factory::class)
class FastingFormViewModel @AssistedInject constructor(
    private val repository: FastingFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("fastingId") private val fastingId: String?,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("fastingId") fastingId: String?,
        ): FastingFormViewModel
    }

    private val _state = MutableStateFlow(
        FastingFormState(
            isEditMode = !fastingId.isNullOrBlank(),
            fastingId = fastingId
        )
    )
    val state: StateFlow<FastingFormState> = _state

    private val _eventChannel = Channel<FastingFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var foodSearchJob: Job? = null
    private var waterSearchJob: Job? = null

    init {
        getConsultation()
    }

    fun onAction(action: FastingFormAction) {
        when (action) {
            FastingFormAction.TryCatalogsAgain -> getConsultation()
            FastingFormAction.SaveFasting -> saveFasting()
            is FastingFormAction.ToggleSaveFastingDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }

            // Food Sheet
            FastingFormAction.OnDismissFoodSheet -> {
                _state.update { it.copy(isFoodSheetOpen = false, foodSearchQuery = "") }
                val filtered = getFilteredAndSortedCatalogs(_state.value.foodCatalogs, "")
                _state.update { it.copy(filteredFoodCatalogs = filtered) }
            }
            FastingFormAction.OnShowFoodSheet -> {
                _state.update { it.copy(isFoodSheetOpen = true) }
            }
            is FastingFormAction.OnSearchFoodQueryChange -> {
                _state.update { it.copy(foodSearchQuery = action.query) }
                foodSearchJob?.cancel()
                foodSearchJob = viewModelScope.launch {
                    delay(300.milliseconds)
                    val filtered = getFilteredAndSortedCatalogs(_state.value.foodCatalogs, action.query)
                    _state.update { it.copy(filteredFoodCatalogs = filtered) }
                }
            }
            is FastingFormAction.OnSelectFood -> {
                _state.update {
                    it.copy(
                        isFoodSheetOpen = false,
                        foodSearchQuery = "",
                        filteredFoodCatalogs = getFilteredAndSortedCatalogs(it.foodCatalogs, ""),
                        formInputState = it.formInputState.copy(selectedFood = action.catalog)
                    )
                }
            }

            // Water Sheet
            FastingFormAction.OnDismissWaterSheet -> {
                _state.update { it.copy(isWaterSheetOpen = false, waterSearchQuery = "") }
                val filtered = getFilteredAndSortedCatalogs(_state.value.waterCatalogs, "")
                _state.update { it.copy(filteredWaterCatalogs = filtered) }
            }
            FastingFormAction.OnShowWaterSheet -> {
                _state.update { it.copy(isWaterSheetOpen = true) }
            }
            is FastingFormAction.OnSearchWaterQueryChange -> {
                _state.update { it.copy(waterSearchQuery = action.query) }
                waterSearchJob?.cancel()
                waterSearchJob = viewModelScope.launch {
                    delay(300.milliseconds)
                    val filtered = getFilteredAndSortedCatalogs(_state.value.waterCatalogs, action.query)
                    _state.update { it.copy(filteredWaterCatalogs = filtered) }
                }
            }
            is FastingFormAction.OnSelectWater -> {
                _state.update {
                    it.copy(
                        isWaterSheetOpen = false,
                        waterSearchQuery = "",
                        filteredWaterCatalogs = getFilteredAndSortedCatalogs(it.waterCatalogs, ""),
                        formInputState = it.formInputState.copy(selectedWater = action.catalog)
                    )
                }
            }

            // Add Catalog
            is FastingFormAction.OnShowAddCatalogDialog -> {
                _state.update {
                    it.copy(
                        showAddAppCatalogDialog = true,
                        activeCatalogTypeId = action.catalogTypeId,
                        activeCatalogTypeName = action.catalogTypeName
                    )
                }
            }
            FastingFormAction.OnDismissAddCatalogDialog -> {
                _state.update {
                    it.copy(
                        showAddAppCatalogDialog = false,
                        activeCatalogTypeId = 0,
                        activeCatalogTypeName = ""
                    )
                }
            }
            is FastingFormAction.OnSaveAppCatalog -> onSaveAppCatalog(action.name)
        }
    }

    private fun getFilteredAndSortedCatalogs(catalogs: List<AppCatalogModel>, query: String): List<AppCatalogModel> {
        val normalizedQuery = query.normalize()
        return if (normalizedQuery.isBlank()) {
            catalogs
        } else {
            catalogs.filter { it.name.normalize().contains(normalizedQuery) }
        }.sortedBy { it.name.lowercase() }
    }

    private fun getConsultation() {
        _state.update { it.copy(isLoadingDataInitial = true) }
        viewModelScope.launch {
            repository.getConsultation(consultationId = consultationId.orEmpty()).fold(
                onSuccess = { consultationWithDetails ->
                    _state.update { it.copy(consultationWithDetails = consultationWithDetails) }
                    getCatalogs()
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(FastingFormEvent.ShowErrorSnackbar("No pudimos obtener la información de la consulta."))
                }
            )
        }
    }

    private fun getCatalogs() {
        viewModelScope.launch {
            repository.getAppCatalogsByTypes(
                types = listOf(Constants.FOOD_FASTING_TYPE_CATALOG, Constants.WATER_FASTING_TYPE_CATALOG)
            ).fold(
                onSuccess = { appCatalogs ->
                    successGetCatalogs(appCatalogs)
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(FastingFormEvent.ShowErrorSnackbar("No pudimos obtener los catálogos."))
                }
            )
        }
    }

    private fun successGetCatalogs(appCatalogs: List<AppCatalogModel>) {
        val foodList = appCatalogs.filter { it.catalogTypeId == Constants.FOOD_FASTING_TYPE_CATALOG }.sortedBy { it.name.lowercase() }
        val waterList = appCatalogs.filter { it.catalogTypeId == Constants.WATER_FASTING_TYPE_CATALOG }.sortedBy { it.name.lowercase() }

        _state.update {
            it.copy(
                foodCatalogs = foodList,
                filteredFoodCatalogs = foodList,
                waterCatalogs = waterList,
                filteredWaterCatalogs = waterList,
                isSuccessGetCatalogs = true
            )
        }
        if (_state.value.isEditMode){
            loadExistingFasting()
        }else{
            _state.update { it.copy(isLoadingDataInitial = false) }
        }

    }

    private fun loadExistingFasting() {
        viewModelScope.launch {
            repository.getFastingByConsultationId(consultationId = consultationId.orEmpty()).fold(
                onSuccess = { fastingWithDetails ->
                    if (fastingWithDetails != null) {
                        val inputs = FastingFormInputsState(
                            selectedFood = fastingWithDetails.foodFasting,
                            selectedWater = fastingWithDetails.waterFasting
                        )
                        _state.update {
                            it.copy(
                                isEditMode = true,
                                existingFastingWithDetails = fastingWithDetails,
                                formInputState = inputs,
                                initialFormInputState = inputs,
                                isLoadingDataInitial = false
                            )
                        }
                    } else {
                        _state.update { it.copy(isLoadingDataInitial = false, isEditMode = false) }
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                }
            )
        }
    }

    private fun onSaveAppCatalog(name: String) {
        val catalogName = name.trim()
        if (catalogName.isBlank()) return

        val typeId = _state.value.activeCatalogTypeId
        if (typeId == 0) return

        _state.update { it.copy(isLoadingAddCatalog = true) }
        viewModelScope.launch {
            val newCatalog = AppCatalogModel(
                id = 0,
                catalogTypeId = typeId,
                name = catalogName,
                description = "",
                isActive = true,
                createdAt = ""
            )

            repository.insertCatalog(catalog = newCatalog).fold(
                onSuccess = { inserted ->
                    _state.update { s ->
                        when (typeId) {
                            Constants.FOOD_FASTING_TYPE_CATALOG -> {
                                val updatedList = (s.foodCatalogs + inserted).sortedBy { it.name.lowercase() }
                                s.copy(
                                    foodCatalogs = updatedList,
                                    filteredFoodCatalogs = getFilteredAndSortedCatalogs(updatedList, s.foodSearchQuery),
                                    formInputState = s.formInputState.copy(selectedFood = inserted),
                                    showAddAppCatalogDialog = false,
                                    isFoodSheetOpen = false,
                                    foodSearchQuery = "",
                                    isLoadingAddCatalog = false
                                )
                            }
                            Constants.WATER_FASTING_TYPE_CATALOG -> {
                                val updatedList = (s.waterCatalogs + inserted).sortedBy { it.name.lowercase() }
                                s.copy(
                                    waterCatalogs = updatedList,
                                    filteredWaterCatalogs = getFilteredAndSortedCatalogs(updatedList, s.waterSearchQuery),
                                    formInputState = s.formInputState.copy(selectedWater = inserted),
                                    showAddAppCatalogDialog = false,
                                    isWaterSheetOpen = false,
                                    waterSearchQuery = "",
                                    isLoadingAddCatalog = false
                                )
                            }
                            else -> s.copy(isLoadingAddCatalog = false)
                        }
                    }
                    _eventChannel.send(FastingFormEvent.ShowToast("Opción \"$catalogName\" agregada y seleccionada."))
                },
                onFailure = {
                    _state.update { it.copy(isLoadingAddCatalog = false) }
                    _eventChannel.send(FastingFormEvent.ShowErrorSnackbar("No se pudo agregar la nueva opción."))
                }
            )
        }
    }

    private fun saveFasting() {
        val currentState = _state.value
        if (currentState.isEditMode) {
            updateFasting()
        } else {
            registerFasting()
        }
    }

    private fun registerFasting() {
        val currentState = _state.value
        _state.update { it.copy(isLoadingSaveFasting = true) }
        viewModelScope.launch {
            repository.saveFasting(
                consultationId = consultationId.orEmpty(),
                foodFastingId = currentState.formInputState.selectedFood?.id ?: 0,
                waterFastingId = currentState.formInputState.selectedWater?.id ?: 0
            ).fold(
                onSuccess = { saved ->
                    val currentForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isEditMode = true,
                            existingFastingWithDetails = saved,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSaveFasting = false
                        )
                    }
                    _eventChannel.send(FastingFormEvent.ShowSuccessSnackbar("Ayuno registrado exitosamente."))
                },
                onFailure = { error ->
                    Log.e("FastingFormVM", "Error al guardar ayuno", error)
                    _state.update { it.copy(isLoadingSaveFasting = false) }
                    _eventChannel.send(FastingFormEvent.ShowErrorSnackbar("No pudimos guardar los tiempos de ayuno. Inténtalo de nuevo."))
                }
            )
        }
    }

    private fun updateFasting() {
        val currentState = _state.value
        val fastingId = currentState.existingFastingWithDetails?.fasting?.id ?: return
        
        _state.update { it.copy(isLoadingUpdateFasting = true) }
        viewModelScope.launch {
            repository.updateFasting(
                id = fastingId,
                consultationId = consultationId.orEmpty(),
                foodFastingId = currentState.formInputState.selectedFood?.id ?: 0,
                waterFastingId = currentState.formInputState.selectedWater?.id ?: 0
            ).fold(
                onSuccess = { updated ->
                    val currentForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isLoadingUpdateFasting = false,
                            existingFastingWithDetails = updated,
                            formInputState = currentForm,
                            initialFormInputState = currentForm
                        )
                    }
                    _eventChannel.send(FastingFormEvent.ShowSuccessSnackbar("Ayuno actualizado correctamente."))
                },
                onFailure = { error ->
                    Log.e("FastingFormVM", "Error al actualizar ayuno", error)
                    _state.update { it.copy(isLoadingUpdateFasting = false) }
                    _eventChannel.send(FastingFormEvent.ShowErrorSnackbar("No pudimos actualizar los tiempos de ayuno. Inténtalo de nuevo."))
                }
            )
        }
    }
}