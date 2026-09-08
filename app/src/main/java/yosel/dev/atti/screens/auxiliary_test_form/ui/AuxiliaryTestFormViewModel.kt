package yosel.dev.atti.screens.auxiliary_test_form.ui

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
import yosel.dev.atti.screens.auxiliary_test_form.domain.AuxiliaryTestFormRepository
import yosel.dev.atti.screens.auxiliary_test_form.ui.AuxiliaryTestFormEvent.ShowErrorSnackbar
import yosel.dev.atti.screens.auxiliary_test_form.ui.AuxiliaryTestFormEvent.ShowSuccessSnackbar
import yosel.dev.atti.screens.auxiliary_test_form.ui.AuxiliaryTestFormEvent.ShowToast
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = AuxiliaryTestFormViewModel.Factory::class)
class AuxiliaryTestFormViewModel @AssistedInject constructor(
    private val repository: AuxiliaryTestFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("auxiliaryTestId") private val auxiliaryTestId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("auxiliaryTestId") auxiliaryTestId: String?
        ): AuxiliaryTestFormViewModel
    }

    private val _state = MutableStateFlow(
        AuxiliaryTestFormState(
            isEditMode = !auxiliaryTestId.isNullOrBlank(),
            auxiliaryTestId = auxiliaryTestId
        )
    )
    val state: StateFlow<AuxiliaryTestFormState> = _state

    private val _eventChannel = Channel<AuxiliaryTestFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        getConsultation()
    }

    fun onAction(action: AuxiliaryTestFormAction) {
        when (action) {
            AuxiliaryTestFormAction.TryCatalogsAgain -> getConsultation()
            AuxiliaryTestFormAction.SaveAuxiliaryTest -> saveAuxiliaryTests()
            is AuxiliaryTestFormAction.ToggleSaveAuxiliaryTestDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }
            is AuxiliaryTestFormAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
                debounceSearch {
                    val currentSelectedIds = _state.value.formInputState.selectedAuxiliaryTests.map { it.id }.toSet()
                    val filtered = getFilteredAndSortedCatalogs(
                        catalogs = _state.value.auxiliaryTestCatalogs,
                        query = action.query,
                        selectedIds = currentSelectedIds
                    )
                    _state.update { s -> s.copy(filteredAuxiliaryTestCatalogs = filtered) }
                }
            }
            is AuxiliaryTestFormAction.OnNewTagNameChange -> {
                _state.update {
                    it.copy(formInputState = it.formInputState.copy(newTagName = action.value))
                }
            }
            AuxiliaryTestFormAction.OnAddNewTag -> onAddNewTag()
            is AuxiliaryTestFormAction.OnToggleAuxiliaryTestOption -> {
                _state.update { s ->
                    val current = s.formInputState.selectedAuxiliaryTests
                    val updated = if (current.any { it.id == action.catalog.id }) {
                        current.filterNot { it.id == action.catalog.id }
                    } else {
                        current + action.catalog
                    }
                    val newSelectedIds = updated.map { it.id }.toSet()
                    val sorted = getFilteredAndSortedCatalogs(
                        catalogs = s.auxiliaryTestCatalogs,
                        query = s.searchQuery,
                        selectedIds = newSelectedIds
                    )
                    s.copy(
                        formInputState = s.formInputState.copy(selectedAuxiliaryTests = updated),
                        filteredAuxiliaryTestCatalogs = sorted
                    )
                }
            }
            is AuxiliaryTestFormAction.OnRemoveAuxiliaryTestOption -> {
                _state.update { s ->
                    val updated = s.formInputState.selectedAuxiliaryTests.filterNot { it.id == action.catalog.id }
                    val newSelectedIds = updated.map { it.id }.toSet()
                    val sorted = getFilteredAndSortedCatalogs(
                        catalogs = s.auxiliaryTestCatalogs,
                        query = s.searchQuery,
                        selectedIds = newSelectedIds
                    )
                    s.copy(
                        formInputState = s.formInputState.copy(selectedAuxiliaryTests = updated),
                        filteredAuxiliaryTestCatalogs = sorted
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
                types = listOf(Constants.AUXILIARY_TEST_TYPE_CATALOG)
            ).fold(
                onSuccess = { appCatalogs ->
                    successGetCatalogs(appCatalogs)
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos obtener los catálogos de pruebas auxiliares."))
                }
            )
        }
    }

    private fun successGetCatalogs(appCatalogs: List<AppCatalogModel>) {
        val auxiliaryTestList = appCatalogs
            .filter { it.catalogTypeId == Constants.AUXILIARY_TEST_TYPE_CATALOG }
            .sortedBy { it.name.lowercase() }

        _state.update { currentState ->
            currentState.copy(
                auxiliaryTestCatalogs = auxiliaryTestList,
                filteredAuxiliaryTestCatalogs = auxiliaryTestList,
                isSuccessGetCatalogs = true
            )
        }

        loadExistingAuxiliaryTests(auxiliaryTestList)
    }

    private fun loadExistingAuxiliaryTests(catalogs: List<AppCatalogModel>) {
        viewModelScope.launch {
            repository.getAuxiliaryTestsByConsultationId(consultationId = consultationId.orEmpty()).fold(
                onSuccess = { testsWithDetails ->
                    val isEdit = testsWithDetails.isNotEmpty() || !auxiliaryTestId.isNullOrBlank()
                    val selectedCatalogs = testsWithDetails.map { it.catalog }
                    val currentSelectedIds = selectedCatalogs.map { it.id }.toSet()

                    val sortedList = getFilteredAndSortedCatalogs(
                        catalogs = catalogs,
                        query = "",
                        selectedIds = currentSelectedIds
                    )

                    val initialInputs = AuxiliaryTestFormInputsState(
                        selectedAuxiliaryTests = selectedCatalogs,
                        newTagName = ""
                    )

                    _state.update { currentState ->
                        currentState.copy(
                            isEditMode = isEdit,
                            existingAuxiliaryTestsWithDetails = testsWithDetails,
                            formInputState = initialInputs,
                            initialFormInputState = initialInputs,
                            filteredAuxiliaryTestCatalogs = sortedList,
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
                catalogTypeId = Constants.AUXILIARY_TEST_TYPE_CATALOG,
                name = tagName,
                description = "",
                isActive = true,
                createdAt = ""
            )

            repository.insertCatalog(catalog = newCatalog).fold(
                onSuccess = { inserted ->
                    _state.update { s ->
                        val updatedCatalogs = (s.auxiliaryTestCatalogs + inserted).sortedBy { it.name.lowercase() }
                        val updatedSelected = s.formInputState.selectedAuxiliaryTests + inserted
                        val currentSelectedIds = updatedSelected.map { it.id }.toSet()
                        val sortedFiltered = getFilteredAndSortedCatalogs(
                            catalogs = updatedCatalogs,
                            query = s.searchQuery,
                            selectedIds = currentSelectedIds
                        )

                        s.copy(
                            auxiliaryTestCatalogs = updatedCatalogs,
                            filteredAuxiliaryTestCatalogs = sortedFiltered,
                            formInputState = s.formInputState.copy(
                                selectedAuxiliaryTests = updatedSelected,
                                newTagName = ""
                            ),
                            isLoadingAddTag = false
                        )
                    }
                    _eventChannel.send(ShowToast("Prueba auxiliar \"$tagName\" agregada y seleccionada."))
                },
                onFailure = {
                    _state.update { it.copy(isLoadingAddTag = false) }
                    _eventChannel.send(ShowErrorSnackbar("No se pudo agregar la nueva etiqueta."))
                }
            )
        }
    }

    private fun saveAuxiliaryTests() {
        val currentState = _state.value
        if (currentState.isEditMode) {
            updateAuxiliaryTests()
        } else {
            registerAuxiliaryTests()
        }
    }

    private fun registerAuxiliaryTests() {
        val currentState = _state.value
        _state.update { it.copy(isLoadingSaveAuxiliaryTest = true) }
        viewModelScope.launch {
            repository.saveAuxiliaryTests(
                consultationId = consultationId.orEmpty(),
                selectedCatalogs = currentState.formInputState.selectedAuxiliaryTests
            ).fold(
                onSuccess = { savedList ->
                    val currentForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isEditMode = true,
                            existingAuxiliaryTestsWithDetails = savedList,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSaveAuxiliaryTest = false
                        )
                    }
                    _eventChannel.send(ShowSuccessSnackbar("Pruebas auxiliares registradas exitosamente."))
                },
                onFailure = { error ->
                    Log.e("AuxiliaryTestFormVM", "Error al guardar pruebas auxiliares", error)
                    _state.update { it.copy(isLoadingSaveAuxiliaryTest = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos guardar las pruebas auxiliares. Inténtalo de nuevo."))
                }
            )
        }
    }

    private fun updateAuxiliaryTests() {
        val currentState = _state.value
        _state.update { it.copy(isLoadingUpdateAuxiliaryTest = true) }
        viewModelScope.launch {
            repository.updateAuxiliaryTests(
                consultationId = consultationId.orEmpty(),
                selectedCatalogs = currentState.formInputState.selectedAuxiliaryTests
            ).fold(
                onSuccess = { updatedList ->
                    val currentForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isLoadingUpdateAuxiliaryTest = false,
                            existingAuxiliaryTestsWithDetails = updatedList,
                            formInputState = currentForm,
                            initialFormInputState = currentForm
                        )
                    }
                    _eventChannel.send(ShowSuccessSnackbar("Pruebas auxiliares actualizadas correctamente."))
                },
                onFailure = { error ->
                    Log.e("AuxiliaryTestFormVM", "Error al actualizar pruebas auxiliares", error)
                    _state.update { it.copy(isLoadingUpdateAuxiliaryTest = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos actualizar las pruebas auxiliares. Inténtalo de nuevo."))
                }
            )
        }
    }
}
