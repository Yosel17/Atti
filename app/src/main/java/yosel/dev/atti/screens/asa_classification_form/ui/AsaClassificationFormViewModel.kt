package yosel.dev.atti.screens.asa_classification_form.ui

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
import yosel.dev.atti.screens.asa_classification_form.domain.AsaClassificationFormRepository
import yosel.dev.atti.screens.asa_classification_form.ui.AsaClassificationFormEvent.ShowErrorSnackbar
import yosel.dev.atti.screens.asa_classification_form.ui.AsaClassificationFormEvent.ShowSuccessSnackbar
import yosel.dev.atti.screens.asa_classification_form.ui.AsaClassificationFormEvent.ShowToast
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = AsaClassificationFormViewModel.Factory::class)
class AsaClassificationFormViewModel @AssistedInject constructor(
    private val repository: AsaClassificationFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("asaClassificationId") private val asaClassificationId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("asaClassificationId") asaClassificationId: String?
        ): AsaClassificationFormViewModel
    }

    private val _state = MutableStateFlow(
        AsaClassificationFormState(
            isEditMode = !asaClassificationId.isNullOrBlank(),
            asaClassificationId = asaClassificationId
        )
    )
    val state: StateFlow<AsaClassificationFormState> = _state

    private val _eventChannel = Channel<AsaClassificationFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        getConsultation()
    }

    fun onAction(action: AsaClassificationFormAction) {
        when (action) {
            AsaClassificationFormAction.TryCatalogsAgain -> getConsultation()
            AsaClassificationFormAction.SaveAsaClassification -> saveAsaClassifications()
            is AsaClassificationFormAction.ToggleSaveAsaClassificationDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }
            is AsaClassificationFormAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
                debounceSearch {
                    val currentSelectedIds = _state.value.formInputState.selectedAsaClassifications.map { it.id }.toSet()
                    val filtered = getFilteredAndSortedCatalogs(
                        catalogs = _state.value.asaClassificationCatalogs,
                        query = action.query,
                        selectedIds = currentSelectedIds
                    )
                    _state.update { s -> s.copy(filteredAsaClassificationCatalogs = filtered) }
                }
            }
            is AsaClassificationFormAction.OnNewTagNameChange -> {
                _state.update {
                    it.copy(formInputState = it.formInputState.copy(newTagName = action.value))
                }
            }
            AsaClassificationFormAction.OnAddNewTag -> onAddNewTag()
            is AsaClassificationFormAction.OnToggleAsaClassificationOption -> {
                _state.update { s ->
                    val current = s.formInputState.selectedAsaClassifications
                    val updated = if (current.any { it.id == action.catalog.id }) {
                        current.filterNot { it.id == action.catalog.id }
                    } else {
                        current + action.catalog
                    }
                    val newSelectedIds = updated.map { it.id }.toSet()
                    val sorted = getFilteredAndSortedCatalogs(
                        catalogs = s.asaClassificationCatalogs,
                        query = s.searchQuery,
                        selectedIds = newSelectedIds
                    )
                    s.copy(
                        formInputState = s.formInputState.copy(selectedAsaClassifications = updated),
                        filteredAsaClassificationCatalogs = sorted
                    )
                }
            }
            is AsaClassificationFormAction.OnRemoveAsaClassificationOption -> {
                _state.update { s ->
                    val updated = s.formInputState.selectedAsaClassifications.filterNot { it.id == action.catalog.id }
                    val newSelectedIds = updated.map { it.id }.toSet()
                    val sorted = getFilteredAndSortedCatalogs(
                        catalogs = s.asaClassificationCatalogs,
                        query = s.searchQuery,
                        selectedIds = newSelectedIds
                    )
                    s.copy(
                        formInputState = s.formInputState.copy(selectedAsaClassifications = updated),
                        filteredAsaClassificationCatalogs = sorted
                    )
                }
            }
        }
    }

    private fun getFilteredAndSortedCatalogs(
        catalogs: List<AppCatalogModel>,
        query: String,
        selectedIds: Set<Int>
    ): List<AppCatalogModel> {
        val normalizedQuery = query.normalize()
        val filtered = if (normalizedQuery.isBlank()) {
            catalogs
        } else {
            catalogs.filter { it.name.normalize().contains(normalizedQuery) }
        }
        return filtered.sortedWith(
            compareByDescending<AppCatalogModel> { selectedIds.contains(it.id) }
                .thenBy { it.name.lowercase() }
        )
    }

    private fun debounceSearch(block: () -> Unit) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300.milliseconds)
            block()
        }
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
                    _eventChannel.send(ShowErrorSnackbar("No pudimos obtener la información de la consulta."))
                }
            )
        }
    }

    private fun getCatalogs() {
        viewModelScope.launch {
            repository.getAppCatalogsByTypes(
                types = listOf(Constants.ASA_CLASSIFICATION_TYPE_CATALOG)
            ).fold(
                onSuccess = { appCatalogs ->
                    successGetCatalogs(appCatalogs)
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos obtener los catálogos de clasificación ASA."))
                }
            )
        }
    }

    private fun successGetCatalogs(appCatalogs: List<AppCatalogModel>) {
        val asaClassificationList = appCatalogs
            .filter { it.catalogTypeId == Constants.ASA_CLASSIFICATION_TYPE_CATALOG }
            .sortedBy { it.name.lowercase() }

        _state.update { currentState ->
            currentState.copy(
                asaClassificationCatalogs = asaClassificationList,
                filteredAsaClassificationCatalogs = asaClassificationList,
                isSuccessGetCatalogs = true
            )
        }

        loadExistingAsaClassifications(asaClassificationList)
    }

    private fun loadExistingAsaClassifications(catalogs: List<AppCatalogModel>) {
        viewModelScope.launch {
            repository.getAsaClassificationsByConsultationId(consultationId = consultationId.orEmpty()).fold(
                onSuccess = { classificationsWithDetails ->
                    val isEdit = classificationsWithDetails.isNotEmpty() || !asaClassificationId.isNullOrBlank()
                    val selectedCatalogs = classificationsWithDetails.map { it.catalog }
                    val currentSelectedIds = selectedCatalogs.map { it.id }.toSet()

                    val sortedList = getFilteredAndSortedCatalogs(
                        catalogs = catalogs,
                        query = "",
                        selectedIds = currentSelectedIds
                    )

                    val initialInputs = AsaClassificationFormInputsState(
                        selectedAsaClassifications = selectedCatalogs,
                        newTagName = ""
                    )

                    _state.update { currentState ->
                        currentState.copy(
                            isEditMode = isEdit,
                            existingAsaClassificationsWithDetails = classificationsWithDetails,
                            formInputState = initialInputs,
                            initialFormInputState = initialInputs,
                            filteredAsaClassificationCatalogs = sortedList,
                            isLoadingDataInitial = false
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                }
            )
        }
    }

    private fun onAddNewTag() {
        val tagName = _state.value.formInputState.newTagName.trim()
        if (tagName.isBlank()) return

        _state.update { it.copy(isLoadingAddTag = true) }
        viewModelScope.launch {
            val newCatalog = AppCatalogModel(
                id = 0,
                catalogTypeId = Constants.ASA_CLASSIFICATION_TYPE_CATALOG,
                name = tagName,
                description = "",
                isActive = true,
                createdAt = ""
            )

            repository.insertCatalog(catalog = newCatalog).fold(
                onSuccess = { inserted ->
                    _state.update { s ->
                        val updatedCatalogs = (s.asaClassificationCatalogs + inserted).sortedBy { it.name.lowercase() }
                        val updatedSelected = s.formInputState.selectedAsaClassifications + inserted
                        val currentSelectedIds = updatedSelected.map { it.id }.toSet()
                        val sortedFiltered = getFilteredAndSortedCatalogs(
                            catalogs = updatedCatalogs,
                            query = s.searchQuery,
                            selectedIds = currentSelectedIds
                        )

                        s.copy(
                            asaClassificationCatalogs = updatedCatalogs,
                            filteredAsaClassificationCatalogs = sortedFiltered,
                            formInputState = s.formInputState.copy(
                                selectedAsaClassifications = updatedSelected,
                                newTagName = ""
                            ),
                            isLoadingAddTag = false
                        )
                    }
                    _eventChannel.send(ShowToast("Clasificación ASA \"$tagName\" agregada y seleccionada."))
                },
                onFailure = {
                    _state.update { it.copy(isLoadingAddTag = false) }
                    _eventChannel.send(ShowErrorSnackbar("No se pudo agregar la nueva etiqueta."))
                }
            )
        }
    }

    private fun saveAsaClassifications() {
        val currentState = _state.value
        if (currentState.isEditMode) {
            updateAsaClassifications()
        } else {
            registerAsaClassifications()
        }
    }

    private fun registerAsaClassifications() {
        val currentState = _state.value
        _state.update { it.copy(isLoadingSaveAsaClassification = true) }
        viewModelScope.launch {
            repository.saveAsaClassifications(
                consultationId = consultationId.orEmpty(),
                selectedCatalogs = currentState.formInputState.selectedAsaClassifications
            ).fold(
                onSuccess = { savedList ->
                    val currentForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isEditMode = true,
                            existingAsaClassificationsWithDetails = savedList,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSaveAsaClassification = false
                        )
                    }
                    _eventChannel.send(ShowSuccessSnackbar("Clasificación ASA registrada exitosamente."))
                },
                onFailure = { error ->
                    Log.e("AsaClassificationFormVM", "Error al guardar clasificación ASA", error)
                    _state.update { it.copy(isLoadingSaveAsaClassification = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos guardar la clasificación ASA. Inténtalo de nuevo."))
                }
            )
        }
    }

    private fun updateAsaClassifications() {
        val currentState = _state.value
        _state.update { it.copy(isLoadingUpdateAsaClassification = true) }
        viewModelScope.launch {
            repository.updateAsaClassifications(
                consultationId = consultationId.orEmpty(),
                selectedCatalogs = currentState.formInputState.selectedAsaClassifications
            ).fold(
                onSuccess = { updatedList ->
                    val currentForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isLoadingUpdateAsaClassification = false,
                            existingAsaClassificationsWithDetails = updatedList,
                            formInputState = currentForm,
                            initialFormInputState = currentForm
                        )
                    }
                    _eventChannel.send(ShowSuccessSnackbar("Clasificación ASA actualizada correctamente."))
                },
                onFailure = { error ->
                    Log.e("AsaClassificationFormVM", "Error al actualizar clasificación ASA", error)
                    _state.update { it.copy(isLoadingUpdateAsaClassification = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos actualizar la clasificación ASA. Inténtalo de nuevo."))
                }
            )
        }
    }
}
