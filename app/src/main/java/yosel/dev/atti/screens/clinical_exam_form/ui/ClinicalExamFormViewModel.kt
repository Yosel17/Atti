package yosel.dev.atti.screens.clinical_exam_form.ui

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
import yosel.dev.atti.screens.clinical_exam_form.domain.ClinicalExamFormRepository
import yosel.dev.atti.screens.clinical_exam_form.ui.ClinicalExamFormEvent.ShowErrorSnackbar
import yosel.dev.atti.screens.clinical_exam_form.ui.ClinicalExamFormEvent.ShowSuccessSnackbar
import yosel.dev.atti.screens.clinical_exam_form.ui.ClinicalExamFormEvent.ShowToast
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = ClinicalExamFormViewModel.Factory::class)
class ClinicalExamFormViewModel @AssistedInject constructor(
    private val repository: ClinicalExamFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("examId") private val examId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("examId") examId: String?
        ): ClinicalExamFormViewModel
    }

    private val _state = MutableStateFlow(
        ClinicalExamFormState(
            isEditMode = !examId.isNullOrBlank(),
            examId = examId
        )
    )
    val state: StateFlow<ClinicalExamFormState> = _state

    private val _eventChannel = Channel<ClinicalExamFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var filterJob: Job? = null

    init {
        getConsultation()
    }

    fun onAction(action: ClinicalExamFormAction) {
        when (action) {
            ClinicalExamFormAction.TryCatalogsAgain -> getConsultation()
            ClinicalExamFormAction.SaveClinicalExam -> saveClinicalExam()

            // Mucosas
            is ClinicalExamFormAction.OnMucousMembranesChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(mucousMembranes = action.value)) }
            }

            // Nódulos Linfáticos
            is ClinicalExamFormAction.OnLymphNodesStatusChange -> {
                _state.update {
                    it.copy(
                        formInputState = it.formInputState.copy(
                            isLymphNodesInfarted = action.isInfarted,
                            selectedLymphNodes = if (!action.isInfarted) emptyList() else it.formInputState.selectedLymphNodes
                        )
                    )
                }
            }
            ClinicalExamFormAction.OnOpenLymphNodesSheet -> {
                val currentSelectedIds = _state.value.formInputState.selectedLymphNodes.map { it.id }.toSet()
                val sorted = getFilteredAndSortedCatalogs(
                    catalogs = _state.value.lymphNodeCatalogs,
                    query = "",
                    selectedIds = currentSelectedIds
                )
                _state.update {
                    it.copy(
                        isLymphNodeSheetOpen = true,
                        lymphNodeSearchQuery = "",
                        filteredLymphNodeCatalogs = sorted
                    )
                }
            }
            ClinicalExamFormAction.OnDismissLymphNodesSheet -> {
                _state.update { it.copy(isLymphNodeSheetOpen = false) }
            }
            is ClinicalExamFormAction.OnSearchLymphNodesQueryChange -> {
                _state.update { it.copy(lymphNodeSearchQuery = action.query) }
                debounceSearch {
                    val currentSelectedIds = _state.value.formInputState.selectedLymphNodes.map { it.id }.toSet()
                    val filtered = getFilteredAndSortedCatalogs(
                        catalogs = _state.value.lymphNodeCatalogs,
                        query = action.query,
                        selectedIds = currentSelectedIds
                    )
                    _state.update { s -> s.copy(filteredLymphNodeCatalogs = filtered) }
                }
            }
            is ClinicalExamFormAction.OnToggleLymphNodeOption -> {
                _state.update { s ->
                    val current = s.formInputState.selectedLymphNodes
                    val updated = if (current.any { it.id == action.catalog.id }) {
                        current.filterNot { it.id == action.catalog.id }
                    } else {
                        current + action.catalog
                    }
                    val newSelectedIds = updated.map { it.id }.toSet()
                    val sorted = getFilteredAndSortedCatalogs(
                        catalogs = s.lymphNodeCatalogs,
                        query = s.lymphNodeSearchQuery,
                        selectedIds = newSelectedIds
                    )
                    s.copy(
                        formInputState = s.formInputState.copy(selectedLymphNodes = updated),
                        filteredLymphNodeCatalogs = sorted
                    )
                }
            }
            is ClinicalExamFormAction.OnRemoveLymphNodeOption -> {
                _state.update { s ->
                    val updated = s.formInputState.selectedLymphNodes.filterNot { it.id == action.catalog.id }
                    val newSelectedIds = updated.map { it.id }.toSet()
                    val sorted = getFilteredAndSortedCatalogs(
                        catalogs = s.lymphNodeCatalogs,
                        query = s.lymphNodeSearchQuery,
                        selectedIds = newSelectedIds
                    )
                    s.copy(
                        formInputState = s.formInputState.copy(selectedLymphNodes = updated),
                        filteredLymphNodeCatalogs = sorted
                    )
                }
            }

            // Pelaje
            ClinicalExamFormAction.OnOpenCoatSheet -> {
                _state.update {
                    it.copy(
                        isCoatSheetOpen = true,
                        coatSearchQuery = "",
                        filteredCoatCatalogs = it.coatCatalogs
                    )
                }
            }
            ClinicalExamFormAction.OnDismissCoatSheet -> {
                _state.update { it.copy(isCoatSheetOpen = false) }
            }
            is ClinicalExamFormAction.OnSearchCoatQueryChange -> {
                _state.update { it.copy(coatSearchQuery = action.query) }
                debounceSearch {
                    val q = action.query.normalize()
                    _state.update { s ->
                        s.copy(filteredCoatCatalogs = if (q.isBlank()) s.coatCatalogs else s.coatCatalogs.filter { it.name.normalize().contains(q) })
                    }
                }
            }
            is ClinicalExamFormAction.OnSelectCoat -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(selectedCoat = action.coat)) }
            }

            // Palpación abdominal
            is ClinicalExamFormAction.OnAbdominalPalpationChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(abdominalPalpation = action.value)) }
            }

            // Condición corporal
            is ClinicalExamFormAction.OnBodyConditionChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(bodyCondition = action.condition)) }
            }

            // Otros hallazgos
            is ClinicalExamFormAction.OnOtherFindingsChange -> {
                _state.update { it.copy(formInputState = it.formInputState.copy(otherFindings = action.value)) }
            }

            // Creación de catálogos
            is ClinicalExamFormAction.OnShowAddCatalogDialog -> {
                _state.update {
                    it.copy(
                        activeCatalogTypeId = action.catalogTypeId,
                        activeCatalogTypeName = action.catalogTypeName,
                        showAddAppCatalogDialog = true
                    )
                }
            }
            ClinicalExamFormAction.OnDismissAddCatalogDialog -> {
                _state.update {
                    it.copy(
                        showAddAppCatalogDialog = false,
                        activeCatalogTypeId = 0,
                        activeCatalogTypeName = ""
                    )
                }
            }
            is ClinicalExamFormAction.OnSaveAppCatalog -> onSaveAppCatalog(action.name)
            is ClinicalExamFormAction.ToggleSaveExamDialog -> {
                _state.update { it.copy(showDialogConfirm = action.show) }
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
                    Constants.COAT_TYPE_CATALOG,
                    Constants.LYMPH_NODE_TYPE_CATALOG
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
        val coatCatalogs = appCatalogs.filter { it.catalogTypeId == Constants.COAT_TYPE_CATALOG }.sortedBy { it.name.lowercase() }
        val lymphNodeCatalogs = appCatalogs.filter { it.catalogTypeId == Constants.LYMPH_NODE_TYPE_CATALOG }.sortedBy { it.name.lowercase() }
        val currentSelectedIds = _state.value.formInputState.selectedLymphNodes.map { it.id }.toSet()
        val sortedLymphNodes = getFilteredAndSortedCatalogs(lymphNodeCatalogs, "", currentSelectedIds)

        _state.update { currentState ->
            currentState.copy(
                coatCatalogs = coatCatalogs,
                filteredCoatCatalogs = coatCatalogs,
                lymphNodeCatalogs = lymphNodeCatalogs,
                filteredLymphNodeCatalogs = sortedLymphNodes,
                isSuccessGetCatalogs = true
            )
        }

        if (!examId.isNullOrBlank()) {
            loadClinicalExamForEdit(id = examId, catalogs = appCatalogs)
        } else {
            _state.update { it.copy(isLoadingDataInitial = false) }
        }
    }

    private fun loadClinicalExamForEdit(id: String, catalogs: List<AppCatalogModel>) {
        viewModelScope.launch {
            repository.getClinicalExamWithDetailsById(id).fold(
                onSuccess = { examWithDetails ->
                    val coat = catalogs.find { it.id == examWithDetails.clinicalExam.coatCatalogId }
                    val selectedNodes = examWithDetails.lymphNodes.map { it.catalog }
                    val isInfarted = selectedNodes.isNotEmpty()
                    val initialForm = ClinicalExamFormInputsState(
                        mucousMembranes = examWithDetails.clinicalExam.mucousMembranes.ifBlank { "Rosadas" },
                        isLymphNodesInfarted = isInfarted,
                        selectedLymphNodes = selectedNodes,
                        selectedCoat = coat ?: examWithDetails.coat.takeIf { it.id != 0 },
                        abdominalPalpation = examWithDetails.clinicalExam.abdominalPalpation,
                        bodyCondition = examWithDetails.clinicalExam.bodyCondition,
                        otherFindings = examWithDetails.clinicalExam.otherFindings
                    )
                    val currentSelectedIds = initialForm.selectedLymphNodes.map { it.id }.toSet()
                    val sortedLymphNodes = getFilteredAndSortedCatalogs(
                        _state.value.lymphNodeCatalogs,
                        "",
                        currentSelectedIds
                    )
                    _state.update { currentState ->
                        currentState.copy(
                            currentExam = examWithDetails.clinicalExam,
                            formInputState = initialForm,
                            initialFormInputState = initialForm,
                            filteredLymphNodeCatalogs = sortedLymphNodes,
                            isLoadingDataInitial = false
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(ShowErrorSnackbar("Error al cargar la información del examen clínico."))
                }
            )
        }
    }

    private fun saveClinicalExam() {
        val currentState = _state.value
        if (currentState.isEditMode) {
            updateClinicalExam()
        } else {
            registerClinicalExam()
        }
    }

    private fun registerClinicalExam() {
        val currentState = _state.value
        _state.update { it.copy(isLoadingSaveExam = true) }
        viewModelScope.launch {
            val examModel = currentState.formInputState.toClinicalExaminationModel(
                consultationId = consultationId ?: ""
            )
            val lymphNodes = currentState.formInputState.toLymphNodeModels()
            repository.saveClinicalExam(
                clinicalExam = examModel,
                lymphNodes = lymphNodes
            ).fold(
                onSuccess = { savedExam ->
                    val currentForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isEditMode = true,
                            examId = savedExam.id,
                            currentExam = savedExam,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSaveExam = false
                        )
                    }
                    _eventChannel.send(ShowSuccessSnackbar("Examen clínico registrado exitosamente."))
                },
                onFailure = {
                    Log.e("ClinicalExamFormVM", "Error al guardar examen clínico", it)
                    _state.update { it.copy(isLoadingSaveExam = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos guardar el examen clínico. Inténtalo de nuevo."))
                }
            )
        }
    }

    private fun updateClinicalExam() {
        val currentState = _state.value
        val currentExam = currentState.currentExam ?: return
        _state.update { it.copy(isLoadingUpdateExam = true) }
        viewModelScope.launch {
            val initial = currentState.initialFormInputState
            val current = currentState.formInputState
            val lymphNodesChanged = current.selectedLymphNodes != initial.selectedLymphNodes ||
                    current.isLymphNodesInfarted != initial.isLymphNodesInfarted

            val updatedExamModel = current.toUpdateModel(
                examId = currentExam.id,
                consultationId = currentExam.consultationId,
                createdAt = currentExam.createdAt,
                status = currentExam.status
            )
            val lymphNodes = if (lymphNodesChanged) current.toLymphNodeModels(currentExam.id) else null

            repository.updateClinicalExamWithDetails(
                clinicalExam = updatedExamModel,
                lymphNodes = lymphNodes
            ).fold(
                onSuccess = {
                    val newForm = currentState.formInputState
                    _state.update { state ->
                        state.copy(
                            isLoadingUpdateExam = false,
                            currentExam = updatedExamModel,
                            formInputState = newForm,
                            initialFormInputState = newForm
                        )
                    }
                    _eventChannel.send(ShowSuccessSnackbar("Examen clínico actualizado correctamente."))
                },
                onFailure = {
                    Log.e("ClinicalExamFormVM", "Error al actualizar examen clínico", it)
                    _state.update { it.copy(isLoadingUpdateExam = false) }
                    _eventChannel.send(ShowErrorSnackbar("No pudimos actualizar el examen clínico. Inténtalo de nuevo."))
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
                        val updatedCoat = if (currentState.activeCatalogTypeId == Constants.COAT_TYPE_CATALOG) {
                            formInputs = formInputs.copy(selectedCoat = inserted)
                            (state.coatCatalogs + inserted).sortedBy { it.name.lowercase() }
                        } else state.coatCatalogs

                        val updatedLymph = if (currentState.activeCatalogTypeId == Constants.LYMPH_NODE_TYPE_CATALOG) {
                            formInputs = formInputs.copy(selectedLymphNodes = formInputs.selectedLymphNodes + inserted)
                            (state.lymphNodeCatalogs + inserted).sortedBy { it.name.lowercase() }
                        } else state.lymphNodeCatalogs

                        val currentSelectedLymphIds = formInputs.selectedLymphNodes.map { it.id }.toSet()
                        val sortedFilteredLymph = getFilteredAndSortedCatalogs(
                            catalogs = updatedLymph,
                            query = state.lymphNodeSearchQuery,
                            selectedIds = currentSelectedLymphIds
                        )

                        state.copy(
                            coatCatalogs = updatedCoat,
                            filteredCoatCatalogs = updatedCoat,
                            lymphNodeCatalogs = updatedLymph,
                            filteredLymphNodeCatalogs = sortedFilteredLymph,
                            formInputState = formInputs,
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