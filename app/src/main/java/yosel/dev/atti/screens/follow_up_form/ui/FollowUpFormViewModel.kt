package yosel.dev.atti.screens.follow_up_form.ui

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
import yosel.dev.atti.core.models.model.FollowUpModel
import yosel.dev.atti.core.models.model.FollowUpWithDetailsModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.follow_up_form.domain.FollowUpFormRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel(assistedFactory = FollowUpFormViewModel.Factory::class)
class FollowUpFormViewModel @AssistedInject constructor(
    private val repository: FollowUpFormRepository,
    @Assisted("consultationId") private val consultationId: String?,
    @Assisted("followUpId") private val followUpId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("consultationId") consultationId: String?,
            @Assisted("followUpId") followUpId: String?
        ): FollowUpFormViewModel
    }

    private val isInitialStandalone = consultationId.isNullOrBlank()

    private val _state = MutableStateFlow(
        FollowUpFormState(
            isEditMode = !followUpId.isNullOrBlank(),
            isStandalone = isInitialStandalone,
            followUpId = followUpId
        )
    )
    val state: StateFlow<FollowUpFormState> = _state

    private val _eventChannel = Channel<FollowUpFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        loadInitialData()
    }

    fun onAction(action: FollowUpFormAction) {
        when (action) {
            FollowUpFormAction.TryLoadAgain -> loadInitialData()
            FollowUpFormAction.SaveFollowUp -> saveFollowUp()
            is FollowUpFormAction.ToggleSaveDialog -> {
                if (action.show && _state.value.isStandalone && !_state.value.formInputState.isStandaloneValid) {
                    _state.update {
                        it.copy(
                            formInputState = it.formInputState.copy(
                                touchedFields = setOf(
                                    FollowUpFormInputsState.FIELD_PATIENT_NAME,
                                    FollowUpFormInputsState.FIELD_CLIENT_NAME,
                                    FollowUpFormInputsState.FIELD_CLIENT_PHONE
                                )
                            )
                        )
                    }
                } else {
                    _state.update { it.copy(showDialogConfirm = action.show) }
                }
            }
            is FollowUpFormAction.ToggleDatePickerDialog -> {
                _state.update { it.copy(showDatePickerDialog = action.show) }
            }
            is FollowUpFormAction.ToggleTimePickerDialog -> {
                _state.update { it.copy(showTimePickerDialog = action.show) }
            }
            is FollowUpFormAction.OnSelectDate -> {
                _state.update {
                    it.copy(
                        formInputState = it.formInputState.copy(
                            selectedDate = action.date,
                            isCustomDateFromPicker = false
                        )
                    )
                }
            }
            is FollowUpFormAction.OnSelectDateForCalendar -> {
                _state.update {
                    it.copy(
                        showDatePickerDialog = false,
                        formInputState = it.formInputState.copy(
                            selectedDate = action.date,
                            isCustomDateFromPicker = true
                        )
                    )
                }
            }
            is FollowUpFormAction.OnSelectTime -> {
                _state.update {
                    it.copy(
                        showTimePickerDialog = false,
                        formInputState = it.formInputState.copy(selectedTime = action.time)
                    )
                }
            }
            FollowUpFormAction.OnResetToDaySelector -> {
                _state.update {
                    it.copy(formInputState = it.formInputState.copy(isCustomDateFromPicker = false))
                }
            }
            is FollowUpFormAction.OnReasonChange -> {
                _state.update {
                    it.copy(formInputState = it.formInputState.copy(reason = action.reason))
                }
            }
            is FollowUpFormAction.OnChangeStandaloneField -> {
                onStandaloneFieldChange(action.value, action.field)
            }
            // BottomSheet Motivos Rápidos
            FollowUpFormAction.OnOpenQuickReasonSheet -> {
                _state.update {
                    it.copy(
                        isQuickReasonSheetOpen = true,
                        quickReasonSearchQuery = "",
                        filteredQuickReasonCatalogs = it.quickReasonCatalogs
                    )
                }
            }
            FollowUpFormAction.OnDismissQuickReasonSheet -> {
                _state.update { it.copy(isQuickReasonSheetOpen = false) }
            }
            is FollowUpFormAction.OnQuickReasonSearchQueryChange -> {
                _state.update { it.copy(quickReasonSearchQuery = action.query) }
                debounceSearch { filterQuickReasons(action.query) }
            }
            is FollowUpFormAction.OnSelectQuickReason -> {
                applyQuickReason(action.catalog.name)
            }
            FollowUpFormAction.OnShowAddQuickReasonDialog -> {
                _state.update { it.copy(showAddQuickReasonDialog = true) }
            }
            FollowUpFormAction.OnDismissAddQuickReasonDialog -> {
                _state.update { it.copy(showAddQuickReasonDialog = false) }
            }
            is FollowUpFormAction.OnSaveQuickReasonCatalog -> saveQuickReasonCatalog(action.name)
        }
    }

    private fun onStandaloneFieldChange(value: String, field: Int) {
        _state.update { currentState ->
            val form = currentState.formInputState
            val updatedForm = when (field) {
                FollowUpFormInputsState.FIELD_PATIENT_NAME -> form.copy(patientName = value)
                FollowUpFormInputsState.FIELD_CLIENT_NAME -> form.copy(clientName = value)
                FollowUpFormInputsState.FIELD_CLIENT_PHONE -> form.copy(clientPhone = value)
                else -> form
            }
            currentState.copy(
                formInputState = updatedForm.copy(
                    touchedFields = updatedForm.touchedFields + field
                )
            )
        }
    }

    private fun loadInitialData() {
        _state.update { it.copy(isLoadingDataInitial = true) }
        val hasConsultation = !consultationId.isNullOrBlank()

        if (hasConsultation) {
            viewModelScope.launch {
                repository.getConsultation(consultationId.orEmpty()).fold(
                    onSuccess = { consultation ->
                        _state.update {
                            it.copy(
                                consultationWithDetails = consultation,
                                isStandalone = false
                            )
                        }
                        loadCatalogsAndFollowUp()
                    },
                    onFailure = {
                        _state.update { it.copy(isLoadingDataInitial = false) }
                        _eventChannel.send(FollowUpFormEvent.ShowErrorSnackbar("No se pudo cargar la información de la consulta."))
                    }
                )
            }
        } else {
            viewModelScope.launch {
                loadCatalogs()
                if (!followUpId.isNullOrBlank()) {
                    loadExistingFollowUpDirect()
                } else {
                    _state.update {
                        it.copy(
                            isStandalone = true,
                            isEditMode = false,
                            isSuccessGetData = true,
                            isLoadingDataInitial = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun loadCatalogs() {
        val catalogsResult = repository.getQuickReasonCatalogs()
        val catalogs = catalogsResult.getOrDefault(emptyList()).sortedBy { it.name.lowercase() }
        _state.update {
            it.copy(
                quickReasonCatalogs = catalogs,
                filteredQuickReasonCatalogs = catalogs
            )
        }
    }

    private fun loadCatalogsAndFollowUp() {
        viewModelScope.launch {
            loadCatalogs()
            _state.update { it.copy(isSuccessGetData = true) }
            if (_state.value.isEditMode) {
                loadExistingFollowUpForConsultation()
            } else {
                _state.update { it.copy(isLoadingDataInitial = false) }
            }
        }
    }

    private fun loadExistingFollowUpForConsultation() {
        viewModelScope.launch {
            val result = if (!followUpId.isNullOrBlank()) {
                repository.getFollowUpById(followUpId)
            } else {
                repository.getFollowUpByConsultationId(consultationId.orEmpty())
            }
            result.fold(
                onSuccess = { existing ->
                    if (existing != null) {
                        applyLoadedFollowUp(existing, isStandalone = false)
                    } else {
                        _state.update { it.copy(isLoadingDataInitial = false) }
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(FollowUpFormEvent.ShowErrorSnackbar("No se pudo cargar la reconsulta previa."))
                }
            )
        }
    }

    private fun loadExistingFollowUpDirect() {
        viewModelScope.launch {
            repository.getFollowUpById(followUpId.orEmpty()).fold(
                onSuccess = { existing ->
                    if (existing != null) {
                        val isStandalone = existing.followUp.consultationId.isNullOrBlank() &&
                                existing.followUp.patientId.isNullOrBlank()
                        applyLoadedFollowUp(existing, isStandalone = isStandalone)
                    } else {
                        _state.update { it.copy(isLoadingDataInitial = false) }
                        _eventChannel.send(FollowUpFormEvent.ShowErrorSnackbar("No se encontró la cita solicitada."))
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(FollowUpFormEvent.ShowErrorSnackbar("No se pudo cargar la cita."))
                }
            )
        }
    }

    private fun applyLoadedFollowUp(existing: FollowUpWithDetailsModel, isStandalone: Boolean) {
        val parsedDateTime = parseIsoToLocalDateTime(existing.followUp.scheduledAt)
        val initialDate = parsedDateTime?.toLocalDate() ?: LocalDate.now()
        val initialTime = (parsedDateTime?.toLocalTime() ?: LocalTime.of(8, 0)).truncatedTo(ChronoUnit.MINUTES)
        val today = LocalDate.now()
        val isOutOfRange = initialDate.isBefore(today.minusDays(30)) || initialDate.isAfter(today.plusDays(30))

        val formState = FollowUpFormInputsState(
            selectedDate = initialDate,
            selectedTime = initialTime,
            isCustomDateFromPicker = isOutOfRange,
            reason = existing.followUp.reason,
            patientName = existing.followUp.patientName.orEmpty(),
            clientName = existing.followUp.clientName.orEmpty(),
            clientPhone = existing.followUp.clientPhone?.ifBlank { "+502 " } ?: "+502 "
        )

        _state.update {
            it.copy(
                isEditMode = true,
                isStandalone = isStandalone,
                followUpId = existing.followUp.id,
                existingFollowUpWithDetails = existing,
                consultationWithDetails = if (!isStandalone) existing.consultationWithDetails else it.consultationWithDetails,
                formInputState = formState,
                initialFormInputState = formState,
                isSuccessGetData = true,
                isLoadingDataInitial = false
            )
        }
    }

    private fun filterQuickReasons(query: String) {
        val q = query.normalize()
        _state.update { s ->
            val filtered = if (q.isBlank()) {
                s.quickReasonCatalogs
            } else {
                s.quickReasonCatalogs.filter { it.name.normalize().contains(q) }
            }
            s.copy(filteredQuickReasonCatalogs = filtered)
        }
    }

    private fun applyQuickReason(reasonText: String) {
        val currentReason = _state.value.formInputState.reason
        val updatedReason = if (currentReason.isBlank()) {
            reasonText
        } else {
            "$currentReason. $reasonText"
        }
        _state.update {
            it.copy(
                isQuickReasonSheetOpen = false,
                formInputState = it.formInputState.copy(reason = updatedReason)
            )
        }
    }

    private fun saveQuickReasonCatalog(name: String) {
        _state.update { it.copy(isLoadingAddQuickReason = true) }
        viewModelScope.launch {
            val newCatalog = AppCatalogModel(
                id = 0,
                catalogTypeId = Constants.QUICK_REASONS_CATALOG_TYPE,
                name = name.trim(),
                description = "",
                isActive = true,
                createdAt = ""
            )
            repository.insertCatalog(newCatalog).fold(
                onSuccess = { inserted ->
                    val updatedList = (_state.value.quickReasonCatalogs + inserted).sortedBy { it.name.lowercase() }
                    _state.update { s ->
                        s.copy(
                            quickReasonCatalogs = updatedList,
                            filteredQuickReasonCatalogs = updatedList,
                            isLoadingAddQuickReason = false,
                            showAddQuickReasonDialog = false
                        )
                    }
                    applyQuickReason(inserted.name)
                    _eventChannel.send(FollowUpFormEvent.ShowToast("Motivo de consulta rápido agregado exitosamente."))
                },
                onFailure = {
                    _state.update { it.copy(isLoadingAddQuickReason = false, showAddQuickReasonDialog = false) }
                    _eventChannel.send(FollowUpFormEvent.ShowToast("No se pudo guardar el motivo de consulta rápido."))
                }
            )
        }
    }

    private fun saveFollowUp() {
        val s = _state.value
        if (s.isStandalone && !s.formInputState.isStandaloneValid) {
            _state.update {
                it.copy(
                    formInputState = it.formInputState.copy(
                        touchedFields = setOf(
                            FollowUpFormInputsState.FIELD_PATIENT_NAME,
                            FollowUpFormInputsState.FIELD_CLIENT_NAME,
                            FollowUpFormInputsState.FIELD_CLIENT_PHONE
                        )
                    )
                )
            }
            return
        }

        if (s.isEditMode) {
            updateExistingFollowUp()
        } else {
            registerNewFollowUp()
        }
    }

    private fun registerNewFollowUp() {
        val s = _state.value
        _state.update { it.copy(isLoadingSaveFollowUp = true) }
        viewModelScope.launch {
            val followUpModel = if (s.isStandalone) {
                FollowUpModel(
                    consultationId = null,
                    patientId = null,
                    scheduledAt = s.formInputState.scheduledAtIso,
                    reason = s.formInputState.reason.trim(),
                    status = Constants.ACTIVE_STATUS,
                    patientName = s.formInputState.patientName.trim(),
                    clientName = s.formInputState.clientName.trim(),
                    clientPhone = s.formInputState.clientPhone.trim()
                )
            } else {
                FollowUpModel(
                    consultationId = consultationId,
                    patientId = s.consultationWithDetails.patientWithDetails.patient.id.takeIf { it.isNotBlank() },
                    scheduledAt = s.formInputState.scheduledAtIso,
                    reason = s.formInputState.reason.trim(),
                    status = Constants.ACTIVE_STATUS,
                    patientName = null,
                    clientName = null,
                    clientPhone = null
                )
            }

            val targetConsultationId = if (s.isStandalone) null else consultationId

            repository.saveFollowUp(targetConsultationId, followUpModel).fold(
                onSuccess = { savedWithDetails ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            followUpId = savedWithDetails.followUp.id,
                            existingFollowUpWithDetails = savedWithDetails,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingSaveFollowUp = false
                        )
                    }
                    val msg = if (s.isStandalone) "Cita agendada exitosamente." else "Reconsulta agendada exitosamente."
                    _eventChannel.send(FollowUpFormEvent.ShowSuccessSnackbar(msg))
                },
                onFailure = { error ->
                    Log.e("FollowUpFormVM", "Error al registrar cita/reconsulta", error)
                    _state.update { it.copy(isLoadingSaveFollowUp = false) }
                    val msg = if (s.isStandalone) "No se pudo agendar la cita." else "No se pudo agendar la reconsulta."
                    _eventChannel.send(FollowUpFormEvent.ShowErrorSnackbar(msg))
                }
            )
        }
    }

    private fun updateExistingFollowUp() {
        val s = _state.value
        val existingId = s.followUpId ?: s.existingFollowUpWithDetails?.followUp?.id ?: return
        _state.update { it.copy(isLoadingUpdateFollowUp = true) }
        viewModelScope.launch {
            val followUpModel = if (s.isStandalone) {
                FollowUpModel(
                    id = existingId,
                    consultationId = null,
                    patientId = null,
                    scheduledAt = s.formInputState.scheduledAtIso,
                    reason = s.formInputState.reason.trim(),
                    createdAt = s.existingFollowUpWithDetails?.followUp?.createdAt.orEmpty(),
                    status = Constants.ACTIVE_STATUS,
                    patientName = s.formInputState.patientName.trim(),
                    clientName = s.formInputState.clientName.trim(),
                    clientPhone = s.formInputState.clientPhone.trim()
                )
            } else {
                FollowUpModel(
                    id = existingId,
                    consultationId = consultationId ?: s.existingFollowUpWithDetails?.followUp?.consultationId,
                    patientId = s.consultationWithDetails.patientWithDetails.patient.id.takeIf { it.isNotBlank() }
                        ?: s.existingFollowUpWithDetails?.followUp?.patientId,
                    scheduledAt = s.formInputState.scheduledAtIso,
                    reason = s.formInputState.reason.trim(),
                    createdAt = s.existingFollowUpWithDetails?.followUp?.createdAt.orEmpty(),
                    status = Constants.ACTIVE_STATUS,
                    patientName = null,
                    clientName = null,
                    clientPhone = null
                )
            }

            val targetConsultationId = if (s.isStandalone) null else (consultationId ?: s.existingFollowUpWithDetails?.followUp?.consultationId)

            repository.updateFollowUp(targetConsultationId, followUpModel).fold(
                onSuccess = { updatedWithDetails ->
                    val currentForm = s.formInputState
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            existingFollowUpWithDetails = updatedWithDetails,
                            formInputState = currentForm,
                            initialFormInputState = currentForm,
                            isLoadingUpdateFollowUp = false
                        )
                    }
                    val msg = if (s.isStandalone) "Cita actualizada correctamente." else "Reconsulta actualizada correctamente."
                    _eventChannel.send(FollowUpFormEvent.ShowSuccessSnackbar(msg))
                },
                onFailure = { error ->
                    Log.e("FollowUpFormVM", "Error al actualizar cita/reconsulta", error)
                    _state.update { it.copy(isLoadingUpdateFollowUp = false) }
                    val msg = if (s.isStandalone) "No se pudo actualizar la cita." else "No se pudo actualizar la reconsulta."
                    _eventChannel.send(FollowUpFormEvent.ShowErrorSnackbar(msg))
                }
            )
        }
    }

    private fun debounceSearch(block: () -> Unit) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300.milliseconds)
            block()
        }
    }

    private fun parseIsoToLocalDateTime(isoString: String): LocalDateTime? {
        if (isoString.isBlank()) return null
        return try {
            var sanitized = isoString.trim().replace(" ", "T")
            if (Regex("[+-]\\d{2}$").containsMatchIn(sanitized)) {
                sanitized += ":00"
            }
            if (sanitized.contains("+") || sanitized.endsWith("Z") || Regex("-\\d{2}:\\d{2}$").containsMatchIn(sanitized)) {
                OffsetDateTime.parse(sanitized)
                    .atZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .truncatedTo(ChronoUnit.MINUTES)
            } else {
                LocalDateTime.parse(sanitized).truncatedTo(ChronoUnit.MINUTES)
            }
        } catch (e: Exception) {
            null
        }
    }
}