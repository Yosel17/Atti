package yosel.dev.atti.screens.anamnesis_form.ui

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
import yosel.dev.atti.core.models.model.AnamnesisDewormingModel
import yosel.dev.atti.core.models.model.AnamnesisDewormingWithDetailsModel
import yosel.dev.atti.core.models.model.AnamnesisVaccineModel
import yosel.dev.atti.core.models.model.AnamnesisVaccineWithDetailsModel
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.core.utils.toAnamnesisFormInputsState
import yosel.dev.atti.core.utils.toUpdateModel
import yosel.dev.atti.screens.anamnesis_form.domain.AnamnesisFormRepository
import yosel.dev.atti.screens.anamnesis_form.ui.AnamnesisFormEvent.ShowErrorSnackbar
import yosel.dev.atti.screens.anamnesis_form.ui.AnamnesisFormEvent.ShowSuccessSnackbar
import yosel.dev.atti.screens.anamnesis_form.ui.AnamnesisFormEvent.ShowToast
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = AnamnesisFormViewModel.Factory::class)
class AnamnesisFormViewModel @AssistedInject constructor(
    private val repository: AnamnesisFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("anamnesisId") private val anamnesisId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("anamnesisId") anamnesisId: String?
        ): AnamnesisFormViewModel
    }

    private val _state = MutableStateFlow(
        AnamnesisFormState(
            isEditMode = !anamnesisId.isNullOrBlank(),
            anamnesisId = anamnesisId
        )
    )
    val state: StateFlow<AnamnesisFormState> = _state

    private val _eventChannel = Channel<AnamnesisFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var filterJob: Job? = null

    init {
        getConsultation()
    }

    fun onAction(action: AnamnesisFormAction) {
        when (action) {
            AnamnesisFormAction.TryCatalogsAgain -> getConsultation()
            AnamnesisFormAction.SaveAnamnesis -> saveAnamnesis()

            // Entorno y rutina
            is AnamnesisFormAction.OnOutdoorAccessChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(hasOutdoorAccess = action.enabled)) }
            }
            AnamnesisFormAction.OnOpenEnvironmentOptionsSheet -> {
                val currentSelectedIds = _state.value.formInputState.selectedEnvironmentOptions.map { it.id }.toSet()
                val sorted = getFilteredAndSortedAnimalLifestyles(
                    lifestyles = _state.value.animalLifestyles,
                    query = "",
                    selectedIds = currentSelectedIds
                )
                _state.update {
                    it.copy(
                        isLifestyleSheetOpen = true,
                        lifestyleSearchQuery = "",
                        filteredAnimalLifestyles = sorted
                    )
                }
            }
            AnamnesisFormAction.OnDismissEnvironmentOptionsSheet -> {
                _state.update { it.copy(isLifestyleSheetOpen = false) }
            }
            is AnamnesisFormAction.OnSearchEnvironmentQueryChange -> {
                _state.update { it.copy(lifestyleSearchQuery = action.query) }
                debounceSearch {
                    val currentSelectedIds = _state.value.formInputState.selectedEnvironmentOptions.map { it.id }.toSet()
                    val filtered = getFilteredAndSortedAnimalLifestyles(
                        lifestyles = _state.value.animalLifestyles,
                        query = action.query,
                        selectedIds = currentSelectedIds
                    )
                    _state.update { s -> s.copy(filteredAnimalLifestyles = filtered) }
                }
            }
            is AnamnesisFormAction.OnToggleEnvironmentOption -> {
                _state.update { s ->
                    val current = s.formInputState.selectedEnvironmentOptions
                    val updated = if (current.any { it.id == action.catalog.id }) {
                        current.filterNot { it.id == action.catalog.id }
                    } else {
                        current + action.catalog
                    }
                    val newSelectedIds = updated.map { it.id }.toSet()
                    val sorted = getFilteredAndSortedAnimalLifestyles(
                        lifestyles = s.animalLifestyles,
                        query = s.lifestyleSearchQuery,
                        selectedIds = newSelectedIds
                    )
                    s.copy(
                        formInputState = s.formInputState.copy(selectedEnvironmentOptions = updated),
                        filteredAnimalLifestyles = sorted
                    )
                }
            }
            is AnamnesisFormAction.OnRemoveEnvironmentOption -> {
                _state.update { s ->
                    val updated = s.formInputState.selectedEnvironmentOptions.filterNot { it.id == action.catalog.id }
                    val newSelectedIds = updated.map { it.id }.toSet()
                    val sorted = getFilteredAndSortedAnimalLifestyles(
                        lifestyles = s.animalLifestyles,
                        query = s.lifestyleSearchQuery,
                        selectedIds = newSelectedIds
                    )
                    s.copy(
                        formInputState = s.formInputState.copy(selectedEnvironmentOptions = updated),
                        filteredAnimalLifestyles = sorted
                    )
                }
            }

            // Profilaxis - Vacunas
            AnamnesisFormAction.OnOpenAddVaccineSheet -> {
                _state.update {
                    it.copy(
                        isAddVaccineSheetOpen = true,
                        tempVaccineIsoDate = "",
                        tempVaccineDisplayDate = "",
                        tempVaccineElapsedText = "",
                        tempSelectedVaccineCatalog = null,
                        tempSelectedScheduleCatalog = null
                    )
                }
            }
            AnamnesisFormAction.OnDismissAddVaccineSheet -> {
                _state.update { it.copy(isAddVaccineSheetOpen = false) }
            }
            is AnamnesisFormAction.OnVaccineDateChange -> {
                _state.update {
                    it.copy(
                        tempVaccineIsoDate = action.dateIso,
                        tempVaccineDisplayDate = action.dateDisplay,
                        tempVaccineElapsedText = action.elapsedText
                    )
                }
            }
            AnamnesisFormAction.OnOpenVaccineNameSheet -> {
                _state.update { it.copy(isVaccineNameSheetOpen = true, vaccineNameSearchQuery = "", filteredVaccineNames = it.vaccineNames) }
            }
            AnamnesisFormAction.OnDismissVaccineNameSheet -> {
                _state.update { it.copy(isVaccineNameSheetOpen = false) }
            }
            is AnamnesisFormAction.OnSearchVaccineNameQueryChange -> {
                _state.update { it.copy(vaccineNameSearchQuery = action.query) }
                debounceSearch {
                    val q = action.query.normalize()
                    _state.update { s ->
                        s.copy(filteredVaccineNames = if (q.isBlank()) s.vaccineNames else s.vaccineNames.filter { it.name.normalize().contains(q) })
                    }
                }
            }
            is AnamnesisFormAction.OnSelectVaccineName -> {
                _state.update { it.copy(tempSelectedVaccineCatalog = action.vaccine) }
            }
            is AnamnesisFormAction.OnSelectVaccineSchedule -> {
                _state.update { it.copy(tempSelectedScheduleCatalog = action.schedule) }
            }
            AnamnesisFormAction.OnSaveVaccineEntry -> {
                val s = _state.value
                if (s.tempSelectedVaccineCatalog == null || s.tempVaccineIsoDate.isBlank() || s.tempSelectedScheduleCatalog == null) {
                    return
                }
                val vaccineEntry = AnamnesisVaccineWithDetailsModel(
                    vaccineEntry = AnamnesisVaccineModel(
                        applicationDate = s.tempVaccineIsoDate,
                        vaccineCatalogId = s.tempSelectedVaccineCatalog.id,
                        schemeCatalogId = s.tempSelectedScheduleCatalog.id
                    ),
                    vaccine = s.tempSelectedVaccineCatalog,
                    scheme = s.tempSelectedScheduleCatalog
                )
                _state.update {
                    it.copy(
                        isAddVaccineSheetOpen = false,
                        formInputState = it.formInputState.copy(
                            vaccines = it.formInputState.vaccines + vaccineEntry
                        )
                    )
                }
                viewModelScope.launch {
                    _eventChannel.send(ShowSuccessSnackbar("Vacuna agregada a la lista correctamente."))
                }
            }
            is AnamnesisFormAction.OnDeleteVaccine -> {
                _state.update { s ->
                    s.copy(formInputState = s.formInputState.copy(vaccines = s.formInputState.vaccines.filterNot { it == action.vaccine }))
                }
            }

            // Profilaxis - Desparasitantes
            AnamnesisFormAction.OnOpenAddDewormingSheet -> {
                val dewormerList = if (_state.value.tempDewormingType == "INTERNO") _state.value.internalDewormers else _state.value.externalDewormers
                _state.update {
                    it.copy(
                        isAddDewormingSheetOpen = true,
                        tempDewormingIsoDate = "",
                        tempDewormingDisplayDate = "",
                        tempDewormingElapsedText = "",
                        tempDewormingType = "INTERNO",
                        tempSelectedDewormerProduct = null,
                        filteredDewormerProducts = dewormerList
                    )
                }
            }
            AnamnesisFormAction.OnDismissAddDewormingSheet -> {
                _state.update { it.copy(isAddDewormingSheetOpen = false) }
            }
            is AnamnesisFormAction.OnDewormingDateChange -> {
                _state.update {
                    it.copy(
                        tempDewormingIsoDate = action.dateIso,
                        tempDewormingDisplayDate = action.dateDisplay,
                        tempDewormingElapsedText = action.elapsedText
                    )
                }
            }
            is AnamnesisFormAction.OnDewormingTypeChange -> {
                val dewormerList = if (action.type == "INTERNO") _state.value.internalDewormers else _state.value.externalDewormers
                _state.update {
                    it.copy(
                        tempDewormingType = action.type,
                        tempSelectedDewormerProduct = null,
                        filteredDewormerProducts = dewormerList
                    )
                }
            }
            AnamnesisFormAction.OnOpenDewormingProductSheet -> {
                val dewormerList = if (_state.value.tempDewormingType == "INTERNO") _state.value.internalDewormers else _state.value.externalDewormers
                _state.update {
                    it.copy(
                        isDewormerProductSheetOpen = true,
                        dewormerProductSearchQuery = "",
                        filteredDewormerProducts = dewormerList
                    )
                }
            }
            AnamnesisFormAction.OnDismissDewormingProductSheet -> {
                _state.update { it.copy(isDewormerProductSheetOpen = false) }
            }
            is AnamnesisFormAction.OnSearchDewormingProductQueryChange -> {
                _state.update { it.copy(dewormerProductSearchQuery = action.query) }
                debounceSearch {
                    val q = action.query.normalize()
                    val base = if (_state.value.tempDewormingType == "INTERNO") _state.value.internalDewormers else _state.value.externalDewormers
                    _state.update { s ->
                        s.copy(filteredDewormerProducts = if (q.isBlank()) base else base.filter { it.name.normalize().contains(q) })
                    }
                }
            }
            is AnamnesisFormAction.OnSelectDewormingProduct -> {
                _state.update { it.copy(tempSelectedDewormerProduct = action.product) }
            }
            AnamnesisFormAction.OnSaveDewormingEntry -> {
                val s = _state.value
                val dewormingEntry = AnamnesisDewormingWithDetailsModel(
                    deworming = AnamnesisDewormingModel(
                        applicationDate = s.tempDewormingIsoDate,
                        dewormingType = s.tempDewormingType,
                        productCatalogId = s.tempSelectedDewormerProduct?.id ?: 0
                    ),
                    product = s.tempSelectedDewormerProduct ?: AppCatalogModel()
                )
                _state.update {
                    it.copy(
                        isAddDewormingSheetOpen = false,
                        formInputState = it.formInputState.copy(
                            dewormings = it.formInputState.dewormings + dewormingEntry
                        )
                    )
                }
                viewModelScope.launch {
                    _eventChannel.send(ShowSuccessSnackbar("Desparasitante agregado a la lista correctamente."))
                }
            }
            is AnamnesisFormAction.OnDeleteDeworming -> {
                _state.update { s ->
                    s.copy(formInputState = s.formInputState.copy(dewormings = s.formInputState.dewormings.filterNot { it == action.deworming }))
                }
            }

            // Compañeros en casa
            is AnamnesisFormAction.OnHousematesChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(housemates = action.value)) }
            }

            // Alimentación
            AnamnesisFormAction.OnOpenConcentrateBrandSheet -> {
                _state.update { it.copy(isConcentrateBrandSheetOpen = true, concentrateBrandSearchQuery = "", filteredConcentrateBrands = it.concentrateBrands) }
            }
            AnamnesisFormAction.OnDismissConcentrateBrandSheet -> {
                _state.update { it.copy(isConcentrateBrandSheetOpen = false) }
            }
            is AnamnesisFormAction.OnSearchConcentrateBrandQueryChange -> {
                _state.update { it.copy(concentrateBrandSearchQuery = action.query) }
                debounceSearch {
                    val q = action.query.normalize()
                    _state.update { s ->
                        s.copy(filteredConcentrateBrands = if (q.isBlank()) s.concentrateBrands else s.concentrateBrands.filter { it.name.normalize().contains(q) })
                    }
                }
            }
            is AnamnesisFormAction.OnSelectConcentrateBrand -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(selectedFoodBrand = action.brand)) }
            }
            AnamnesisFormAction.OnOpenConcentrateUnitSheet -> {
                _state.update { it.copy(isConcentrateUnitSheetOpen = true, concentrateUnitSearchQuery = "", filteredConcentrateUnits = it.concentrateUnitsOfMeasurement) }
            }
            AnamnesisFormAction.OnDismissConcentrateUnitSheet -> {
                _state.update { it.copy(isConcentrateUnitSheetOpen = false) }
            }
            is AnamnesisFormAction.OnSearchConcentrateUnitQueryChange -> {
                _state.update { it.copy(concentrateUnitSearchQuery = action.query) }
                debounceSearch {
                    val q = action.query.normalize()
                    _state.update { s ->
                        s.copy(filteredConcentrateUnits = if (q.isBlank()) s.concentrateUnitsOfMeasurement else s.concentrateUnitsOfMeasurement.filter { it.name.normalize().contains(q) })
                    }
                }
            }
            is AnamnesisFormAction.OnSelectConcentrateUnit -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(selectedFoodUnit = action.unit)) }
            }
            is AnamnesisFormAction.OnFoodQuantityChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(foodQuantity = action.quantity)) }
            }
            is AnamnesisFormAction.OnHomemadeFoodToggle -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(hasHomemadeFood = action.enabled)) }
            }
            is AnamnesisFormAction.OnHomemadeFoodDetailsChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(homemadeFoodDetails = action.details)) }
            }
            is AnamnesisFormAction.OnFeedingFrequencyChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(feedingFrequency = action.frequency)) }
            }
            is AnamnesisFormAction.OnWaterConsumptionChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(waterConsumption = action.consumption)) }
            }

            // Catálogos
            is AnamnesisFormAction.OnShowAddCatalogDialog -> {
                _state.update {
                    it.copy(
                        activeCatalogTypeId = action.catalogTypeId,
                        activeCatalogTypeName = action.catalogTypeName,
                        showAddAppCatalogDialog = true
                    )
                }
            }
            AnamnesisFormAction.OnDismissAddCatalogDialog -> {
                _state.update {
                    it.copy(
                        showAddAppCatalogDialog = false,
                        activeCatalogTypeId = 0,
                        activeCatalogTypeName = ""
                    )
                }
            }
            is AnamnesisFormAction.OnSaveAppCatalog -> onSaveAppCatalog(action.name)
            is AnamnesisFormAction.ToggleSaveAnamnesisDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }
        }
    }

    private fun getFilteredAndSortedAnimalLifestyles(
        lifestyles: List<AppCatalogModel>,
        query: String,
        selectedIds: Set<Int>
    ): List<AppCatalogModel> {
        val normalizedQuery = query.normalize()
        val filtered = if (normalizedQuery.isBlank()) {
            lifestyles
        } else {
            lifestyles.filter { it.name.normalize().contains(normalizedQuery) }
        }
        return filtered.sortedWith(
            compareByDescending<AppCatalogModel> { selectedIds.contains(it.id) }
                .thenBy { it.name.lowercase() }
        )
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
                    _eventChannel.send(ShowErrorSnackbar("No pudimos obtener la información de la consulta."))
                }
            )
        }
    }

    private fun getCatalogs() {
        viewModelScope.launch {
            repository.getAppCatalogsByTypes(
                types = listOf(
                    Constants.ANIMAL_LIFESTYLE_TYPE_CATALOG,
                    Constants.VACCINE_NAME_TYPE_CATALOG,
                    Constants.VACCINATION_SCHEDULE_TYPE_CATALOG,
                    Constants.INTERNAL_DEWORMER_TYPE_CATALOG,
                    Constants.EXTERNAL_DEWORMER_TYPE_CATALOG,
                    Constants.CONCENTRATE_BRAND_TYPE_CATALOG,
                    Constants.CONCENTRATE_UNIT_OF_MEASURE_TYPE_CATALOG
                )
            ).fold(
                onSuccess = { appCatalogs ->
                    successGetCatalogs(appCatalogs)
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos obtener los catálogos. Inténtalo de nuevo."))
                }
            )
        }
    }

    private fun successGetCatalogs(appCatalogs: List<AppCatalogModel>) {
        val animalLifestyles = appCatalogs.filter { it.catalogTypeId == Constants.ANIMAL_LIFESTYLE_TYPE_CATALOG }.sortedBy { it.name.lowercase() }
        val vaccineNames = appCatalogs.filter { it.catalogTypeId == Constants.VACCINE_NAME_TYPE_CATALOG }.sortedBy { it.name.lowercase() }
        val vaccinationSchedules = appCatalogs.filter { it.catalogTypeId == Constants.VACCINATION_SCHEDULE_TYPE_CATALOG }.sortedBy { it.name.lowercase() }
        val internalDewormers = appCatalogs.filter { it.catalogTypeId == Constants.INTERNAL_DEWORMER_TYPE_CATALOG }.sortedBy { it.name.lowercase() }
        val externalDewormers = appCatalogs.filter { it.catalogTypeId == Constants.EXTERNAL_DEWORMER_TYPE_CATALOG }.sortedBy { it.name.lowercase() }
        val concentrateBrands = appCatalogs.filter { it.catalogTypeId == Constants.CONCENTRATE_BRAND_TYPE_CATALOG }.sortedBy { it.name.lowercase() }
        val concentrateUnitsOfMeasurement = appCatalogs.filter { it.catalogTypeId == Constants.CONCENTRATE_UNIT_OF_MEASURE_TYPE_CATALOG }.sortedBy { it.name.lowercase() }

        val currentSelectedIds = _state.value.formInputState.selectedEnvironmentOptions.map { it.id }.toSet()
        val sortedAnimalLifestyles = getFilteredAndSortedAnimalLifestyles(animalLifestyles, "", currentSelectedIds)

        _state.update { currentState ->
            currentState.copy(
                animalLifestyles = animalLifestyles,
                filteredAnimalLifestyles = sortedAnimalLifestyles,
                vaccineNames = vaccineNames,
                filteredVaccineNames = vaccineNames,
                vaccinationSchedules = vaccinationSchedules,
                internalDewormers = internalDewormers,
                externalDewormers = externalDewormers,
                filteredDewormerProducts = internalDewormers,
                concentrateBrands = concentrateBrands,
                filteredConcentrateBrands = concentrateBrands,
                concentrateUnitsOfMeasurement = concentrateUnitsOfMeasurement,
                filteredConcentrateUnits = concentrateUnitsOfMeasurement,
                isSuccessGetCatalogs = true
            )
        }

        if (!anamnesisId.isNullOrBlank()) {
            loadAnamnesisForEdit(id = anamnesisId, catalogs = appCatalogs)
        } else {
            _state.update { it.copy(isLoadingDataInitial = false) }
        }
    }

    private fun loadAnamnesisForEdit(id: String, catalogs: List<AppCatalogModel>) {
        viewModelScope.launch {
            repository.getAnamnesisWithDetailsByIdRoom(id).fold(
                onSuccess = { anamnesisWithDetails ->
                    val foodBrand = catalogs.find { it.id == anamnesisWithDetails.anamnesis.foodBrandId }
                    val foodUnit = catalogs.find { it.id == anamnesisWithDetails.anamnesis.foodUnitTypeId }
                    val initialForm = anamnesisWithDetails.toAnamnesisFormInputsState(
                        foodBrand = foodBrand,
                        foodUnit = foodUnit
                    )
                    val currentSelectedIds = initialForm.selectedEnvironmentOptions.map { it.id }.toSet()
                    val sortedLifestyles = getFilteredAndSortedAnimalLifestyles(
                        _state.value.animalLifestyles,
                        "",
                        currentSelectedIds
                    )
                    _state.update { currentState ->
                        currentState.copy(
                            currentAnamnesis = anamnesisWithDetails.anamnesis,
                            formInputState = initialForm,
                            initialFormInputState = initialForm,
                            filteredAnimalLifestyles = sortedLifestyles,
                            isLoadingDataInitial = false
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(
                        ShowErrorSnackbar("Error al cargar la información de la anamnesis.")
                    )
                }
            )
        }
    }

    private fun saveAnamnesis() {
        val currentState = _state.value
        if (currentState.isEditMode) {
            updateAnamnesis()
        } else {
            registerAnamnesis()
        }
    }

    private fun registerAnamnesis() {
        val currentState = _state.value
        _state.update { it.copy(isLoadingSaveAnamnesis = true) }
        viewModelScope.launch {
            val anamnesisModel = currentState.formInputState.toAnamnesisModel(
                consultationId = consultationId ?: ""
            )
            val envOptions = currentState.formInputState.toEnvironmentOptionModels()
            val vaccines = currentState.formInputState.toVaccineModels()
            val dewormings = currentState.formInputState.toDewormingModels()

            repository.saveAnamnesis(
                anamnesis = anamnesisModel,
                environmentOptions = envOptions,
                vaccines = vaccines,
                dewormings = dewormings
            ).fold(
                onSuccess = { savedAnamnesis ->
                    val currentForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isEditMode = true,
                            anamnesisId = savedAnamnesis.id,
                            currentAnamnesis = savedAnamnesis,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSaveAnamnesis = false
                        )
                    }
                    _eventChannel.send(ShowSuccessSnackbar("Anamnesis registrada exitosamente."))
                },
                onFailure = {
                    Log.e("AnamnesisFormViewModel", "Error al guardar anamnesis", it)
                    _state.update { it.copy(isLoadingSaveAnamnesis = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos guardar la anamnesis. Inténtalo de nuevo."))
                }
            )
        }
    }

    private fun updateAnamnesis() {
        val currentState = _state.value
        val currentAnamnesis = currentState.currentAnamnesis ?: return
        _state.update { it.copy(isLoadingUpdateAnamnesis = true) }
        viewModelScope.launch {
            val updatedAnamnesisModel = currentState.formInputState.toUpdateModel(
                anamnesisId = currentAnamnesis.id,
                consultationId = currentAnamnesis.consultationId,
                createdAt = currentAnamnesis.createdAt,
                status = currentAnamnesis.status
            )
            val envOptions = currentState.formInputState.toEnvironmentOptionModels(anamnesisId = currentAnamnesis.id)
            val vaccines = currentState.formInputState.toVaccineModels(anamnesisId = currentAnamnesis.id)
            val dewormings = currentState.formInputState.toDewormingModels(anamnesisId = currentAnamnesis.id)

            repository.updateAnamnesisWithDetails(
                anamnesis = updatedAnamnesisModel,
                environmentOptions = envOptions,
                vaccines = vaccines,
                dewormings = dewormings
            ).fold(
                onSuccess = {
                    val newForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isLoadingUpdateAnamnesis = false,
                            currentAnamnesis = updatedAnamnesisModel,
                            formInputState = newForm,
                            initialFormInputState = newForm
                        )
                    }
                    _eventChannel.send(ShowSuccessSnackbar("Anamnesis actualizada correctamente."))
                },
                onFailure = {
                    Log.e("AnamnesisFormViewModel", "Error al actualizar la anamnesis", it)
                    _state.update { it.copy(isLoadingUpdateAnamnesis = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos actualizar la anamnesis. Inténtalo de nuevo."))
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
                        var formInputs = state.formInputState
                        var tempVaccineName = state.tempSelectedVaccineCatalog
                        var tempVaccineSchedule = state.tempSelectedScheduleCatalog
                        var tempDewormer = state.tempSelectedDewormerProduct

                        val updatedLifestyles = if (currentState.activeCatalogTypeId == Constants.ANIMAL_LIFESTYLE_TYPE_CATALOG) {
                            (state.animalLifestyles + inserted).sortedBy { it.name.lowercase() }
                        } else state.animalLifestyles

                        val currentSelectedLifestyleIds = formInputs.selectedEnvironmentOptions.map { it.id }.toSet()
                        val sortedFilteredLifestyles = getFilteredAndSortedAnimalLifestyles(
                            lifestyles = updatedLifestyles,
                            query = state.lifestyleSearchQuery,
                            selectedIds = currentSelectedLifestyleIds
                        )

                        val updatedVaccineNames = if (currentState.activeCatalogTypeId == Constants.VACCINE_NAME_TYPE_CATALOG) {
                            tempVaccineName = inserted
                            (state.vaccineNames + inserted).sortedBy { it.name.lowercase() }
                        } else state.vaccineNames

                        val updatedSchedules = if (currentState.activeCatalogTypeId == Constants.VACCINATION_SCHEDULE_TYPE_CATALOG) {
                            tempVaccineSchedule = inserted
                            (state.vaccinationSchedules + inserted).sortedBy { it.name.lowercase() }
                        } else state.vaccinationSchedules

                        val updatedInternal = if (currentState.activeCatalogTypeId == Constants.INTERNAL_DEWORMER_TYPE_CATALOG) {
                            tempDewormer = inserted
                            (state.internalDewormers + inserted).sortedBy { it.name.lowercase() }
                        } else state.internalDewormers

                        val updatedExternal = if (currentState.activeCatalogTypeId == Constants.EXTERNAL_DEWORMER_TYPE_CATALOG) {
                            tempDewormer = inserted
                            (state.externalDewormers + inserted).sortedBy { it.name.lowercase() }
                        } else state.externalDewormers

                        val updatedBrands = if (currentState.activeCatalogTypeId == Constants.CONCENTRATE_BRAND_TYPE_CATALOG) {
                            formInputs = formInputs.copy(selectedFoodBrand = inserted)
                            (state.concentrateBrands + inserted).sortedBy { it.name.lowercase() }
                        } else state.concentrateBrands

                        val updatedUnits = if (currentState.activeCatalogTypeId == Constants.CONCENTRATE_UNIT_OF_MEASURE_TYPE_CATALOG) {
                            formInputs = formInputs.copy(selectedFoodUnit = inserted)
                            (state.concentrateUnitsOfMeasurement + inserted).sortedBy { it.name.lowercase() }
                        } else state.concentrateUnitsOfMeasurement

                        state.copy(
                            animalLifestyles = updatedLifestyles,
                            filteredAnimalLifestyles = sortedFilteredLifestyles,
                            vaccineNames = updatedVaccineNames,
                            filteredVaccineNames = updatedVaccineNames,
                            vaccinationSchedules = updatedSchedules,
                            internalDewormers = updatedInternal,
                            externalDewormers = updatedExternal,
                            filteredDewormerProducts = if (state.tempDewormingType == "INTERNO") updatedInternal else updatedExternal,
                            concentrateBrands = updatedBrands,
                            filteredConcentrateBrands = updatedBrands,
                            concentrateUnitsOfMeasurement = updatedUnits,
                            filteredConcentrateUnits = updatedUnits,
                            formInputState = formInputs,
                            tempSelectedVaccineCatalog = tempVaccineName,
                            tempSelectedScheduleCatalog = tempVaccineSchedule,
                            tempSelectedDewormerProduct = tempDewormer,
                            isLoadingAddCatalog = false,
                            showAddAppCatalogDialog = false
                        )
                    }
                    _eventChannel.send(ShowToast("${currentState.activeCatalogTypeName} agregado correctamente."))
                },
                onFailure = {
                    _state.update { it.copy(isLoadingAddCatalog = false, showAddAppCatalogDialog = false) }
                    _eventChannel.send(ShowToast("No se pudo agregar el catálogo."))
                }
            )
        }
    }
}