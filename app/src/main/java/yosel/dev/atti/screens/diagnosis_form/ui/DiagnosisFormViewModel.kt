package yosel.dev.atti.screens.diagnosis_form.ui

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
import yosel.dev.atti.screens.diagnosis_form.domain.DiagnosisFormRepository
import yosel.dev.atti.screens.diagnosis_form.ui.DiagnosisFormEvent.ShowErrorSnackbar
import yosel.dev.atti.screens.diagnosis_form.ui.DiagnosisFormEvent.ShowSuccessSnackbar
import yosel.dev.atti.screens.diagnosis_form.ui.DiagnosisFormEvent.ShowToast
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = DiagnosisFormViewModel.Factory::class)
class DiagnosisFormViewModel @AssistedInject constructor(
    private val repository: DiagnosisFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("diagnosisId") private val diagnosisId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("diagnosisId") diagnosisId: String?
        ): DiagnosisFormViewModel
    }

    private val _state = MutableStateFlow(
        DiagnosisFormState(
            isEditMode = !diagnosisId.isNullOrBlank(),
            diagnosisId = diagnosisId
        )
    )
    val state: StateFlow<DiagnosisFormState> = _state

    private val _eventChannel = Channel<DiagnosisFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        getConsultation()
    }

    fun onAction(action: DiagnosisFormAction) {
        when (action) {
            DiagnosisFormAction.TryCatalogsAgain -> getConsultation()
            DiagnosisFormAction.SaveDiagnosis -> saveDiagnoses()
            is DiagnosisFormAction.ToggleSaveDiagnosisDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
            }
            is DiagnosisFormAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
                debounceSearch {
                    val currentSelectedIds = _state.value.formInputState.selectedDiagnoses.map { it.id }.toSet()
                    val filtered = getFilteredAndSortedCatalogs(
                        catalogs = _state.value.diagnosisCatalogs,
                        query = action.query,
                        selectedIds = currentSelectedIds
                    )
                    _state.update { s -> s.copy(filteredDiagnosisCatalogs = filtered) }
                }
            }
            is DiagnosisFormAction.OnNewTagNameChange -> {
                _state.update {
                    it.copy(formInputState = it.formInputState.copy(newTagName = action.value))
                }
            }
            DiagnosisFormAction.OnAddNewTag -> onAddNewTag()
            is DiagnosisFormAction.OnToggleDiagnosisOption -> {
                _state.update { s ->
                    val current = s.formInputState.selectedDiagnoses
                    val updated = if (current.any { it.id == action.catalog.id }) {
                        current.filterNot { it.id == action.catalog.id }
                    } else {
                        current + action.catalog
                    }
                    val newSelectedIds = updated.map { it.id }.toSet()
                    val sorted = getFilteredAndSortedCatalogs(
                        catalogs = s.diagnosisCatalogs,
                        query = s.searchQuery,
                        selectedIds = newSelectedIds
                    )
                    s.copy(
                        formInputState = s.formInputState.copy(selectedDiagnoses = updated),
                        filteredDiagnosisCatalogs = sorted
                    )
                }
            }
            is DiagnosisFormAction.OnRemoveDiagnosisOption -> {
                _state.update { s ->
                    val updated = s.formInputState.selectedDiagnoses.filterNot { it.id == action.catalog.id }
                    val newSelectedIds = updated.map { it.id }.toSet()
                    val sorted = getFilteredAndSortedCatalogs(
                        catalogs = s.diagnosisCatalogs,
                        query = s.searchQuery,
                        selectedIds = newSelectedIds
                    )
                    s.copy(
                        formInputState = s.formInputState.copy(selectedDiagnoses = updated),
                        filteredDiagnosisCatalogs = sorted
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
                types = listOf(Constants.DIAGNOSIS_TYPE_CATALOG)
            ).fold(
                onSuccess = { appCatalogs ->
                    successGetCatalogs(appCatalogs)
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos obtener los catálogos de diagnóstico."))
                }
            )
        }
    }

    private fun successGetCatalogs(appCatalogs: List<AppCatalogModel>) {
        val diagnosisList = appCatalogs
            .filter { it.catalogTypeId == Constants.DIAGNOSIS_TYPE_CATALOG }
            .sortedBy { it.name.lowercase() }

        _state.update { currentState ->
            currentState.copy(
                diagnosisCatalogs = diagnosisList,
                filteredDiagnosisCatalogs = diagnosisList,
                isSuccessGetCatalogs = true
            )
        }

        loadExistingDiagnoses(diagnosisList)
    }

    private fun loadExistingDiagnoses(catalogs: List<AppCatalogModel>) {
        viewModelScope.launch {
            repository.getDiagnosesByConsultationId(consultationId = consultationId.orEmpty()).fold(
                onSuccess = { diagnosesWithDetails ->
                    val isEdit = diagnosesWithDetails.isNotEmpty() || !diagnosisId.isNullOrBlank()
                    val selectedCatalogs = diagnosesWithDetails.map { it.catalog }
                    val currentSelectedIds = selectedCatalogs.map { it.id }.toSet()

                    val sortedList = getFilteredAndSortedCatalogs(
                        catalogs = catalogs,
                        query = "",
                        selectedIds = currentSelectedIds
                    )

                    val initialInputs = DiagnosisFormInputsState(
                        selectedDiagnoses = selectedCatalogs,
                        newTagName = ""
                    )

                    _state.update { currentState ->
                        currentState.copy(
                            isEditMode = isEdit,
                            existingDiagnosesWithDetails = diagnosesWithDetails,
                            formInputState = initialInputs,
                            initialFormInputState = initialInputs,
                            filteredDiagnosisCatalogs = sortedList,
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
                catalogTypeId = Constants.DIAGNOSIS_TYPE_CATALOG,
                name = tagName,
                description = "",
                isActive = true,
                createdAt = ""
            )

            repository.insertCatalog(catalog = newCatalog).fold(
                onSuccess = { inserted ->
                    _state.update { s ->
                        val updatedCatalogs = (s.diagnosisCatalogs + inserted).sortedBy { it.name.lowercase() }
                        val updatedSelected = s.formInputState.selectedDiagnoses + inserted
                        val currentSelectedIds = updatedSelected.map { it.id }.toSet()
                        val sortedFiltered = getFilteredAndSortedCatalogs(
                            catalogs = updatedCatalogs,
                            query = s.searchQuery,
                            selectedIds = currentSelectedIds
                        )

                        s.copy(
                            diagnosisCatalogs = updatedCatalogs,
                            filteredDiagnosisCatalogs = sortedFiltered,
                            formInputState = s.formInputState.copy(
                                selectedDiagnoses = updatedSelected,
                                newTagName = ""
                            ),
                            isLoadingAddTag = false
                        )
                    }
                    _eventChannel.send(ShowToast("Diagnóstico \"$tagName\" agregado y seleccionado."))
                },
                onFailure = {
                    _state.update { it.copy(isLoadingAddTag = false) }
                    _eventChannel.send(ShowErrorSnackbar("No se pudo agregar la nueva etiqueta."))
                }
            )
        }
    }

    private fun saveDiagnoses() {
        val currentState = _state.value
        if (currentState.isEditMode) {
            updateDiagnoses()
        } else {
            registerDiagnoses()
        }
    }

    private fun registerDiagnoses() {
        val currentState = _state.value
        _state.update { it.copy(isLoadingSaveDiagnosis = true) }
        viewModelScope.launch {
            repository.saveDiagnoses(
                consultationId = consultationId.orEmpty(),
                selectedCatalogs = currentState.formInputState.selectedDiagnoses
            ).fold(
                onSuccess = { savedList ->
                    val currentForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isEditMode = true,
                            existingDiagnosesWithDetails = savedList,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSaveDiagnosis = false
                        )
                    }
                    _eventChannel.send(ShowSuccessSnackbar("Diagnósticos registrados exitosamente."))
                },
                onFailure = { error ->
                    Log.e("DiagnosisFormVM", "Error al guardar diagnósticos", error)
                    _state.update { it.copy(isLoadingSaveDiagnosis = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos guardar los diagnósticos. Inténtalo de nuevo."))
                }
            )
        }
    }

    private fun updateDiagnoses() {
        val currentState = _state.value
        _state.update { it.copy(isLoadingUpdateDiagnosis = true) }
        viewModelScope.launch {
            repository.updateDiagnoses(
                consultationId = consultationId.orEmpty(),
                selectedCatalogs = currentState.formInputState.selectedDiagnoses
            ).fold(
                onSuccess = { updatedList ->
                    val currentForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isLoadingUpdateDiagnosis = false,
                            existingDiagnosesWithDetails = updatedList,
                            formInputState = currentForm,
                            initialFormInputState = currentForm
                        )
                    }
                    _eventChannel.send(ShowSuccessSnackbar("Diagnósticos actualizados correctamente."))
                },
                onFailure = { error ->
                    Log.e("DiagnosisFormVM", "Error al actualizar diagnósticos", error)
                    _state.update { it.copy(isLoadingUpdateDiagnosis = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos actualizar los diagnósticos. Inténtalo de nuevo."))
                }
            )
        }
    }
}