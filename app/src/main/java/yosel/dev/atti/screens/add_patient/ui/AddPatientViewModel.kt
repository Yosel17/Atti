package yosel.dev.atti.screens.add_patient.ui

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
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ClientModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.core.utils.toAddPatientFormState
import yosel.dev.atti.core.utils.toInsertModel
import yosel.dev.atti.core.utils.toUpdateModel
import yosel.dev.atti.screens.add_patient.domain.AddPatientRepository
import javax.inject.Inject

@HiltViewModel(assistedFactory = AddPatientViewModel.Factory::class)
class AddPatientViewModel @AssistedInject constructor(
    private val repository: AddPatientRepository,
    @Assisted("patientId") private val patientId: String?,
    @Assisted("clienteId") private val clienteId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("patientId") patientId: String?,
            @Assisted("clienteId") clienteId: String?
        ): AddPatientViewModel
    }

    private val _state = MutableStateFlow(
        AddPatientState(
            isEditMode = !patientId.isNullOrBlank(),
            patientId = patientId
        )
    )
    val state: StateFlow<AddPatientState> = _state

    private val _eventChannel = Channel<AddPatientEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        getCatalogsAndClients()
    }

    fun onAction(action: AddPatientAction) {
        when (action) {
            AddPatientAction.RegisterPatient -> savePatient()
            AddPatientAction.TryCatalogsAgain -> getCatalogsAndClients()
            is AddPatientAction.OnChangeValueFormState -> {
                _state.update {
                    it.copy(
                        formState = it.formState.copy(
                            touchedFields = it.formState.touchedFields + action.field
                        ).let { form ->
                            when (action.field) {
                                Constants.PATIENT_NAME_FIELD -> form.copy(name = action.value)
                                Constants.PATIENT_BREED_FIELD -> form.copy(breed = action.value)
                                Constants.PATIENT_AGE_YEARS_FIELD -> form.copy(ageYears = action.value)
                                Constants.PATIENT_AGE_MONTHS_FIELD -> form.copy(ageMonths = action.value)
                                Constants.PATIENT_COLOR_FIELD -> form.copy(color = action.value)
                                else -> form
                            }
                        }
                    )
                }
            }
            is AddPatientAction.OnSelectSpecies -> {
                _state.update { it.copy(formState = it.formState.copy(speciesId = action.id)) }
            }
            is AddPatientAction.OnSelectGender -> {
                _state.update { it.copy(formState = it.formState.copy(genderId = action.id)) }
            }
            is AddPatientAction.OnToggleNeutered -> {
                _state.update { it.copy(formState = it.formState.copy(isNeutered = action.value)) }
            }
            AddPatientAction.OnOpenClientSheet -> {
                _state.update { it.copy(isClientSheetOpen = true, clientSearchQuery = "") }
                filterClients("")
            }
            AddPatientAction.OnDismissClientSheet -> {
                _state.update { it.copy(isClientSheetOpen = false) }
            }
            is AddPatientAction.OnSearchClientQueryChange -> {
                _state.update { it.copy(clientSearchQuery = action.query) }
                filterClients(action.query)
            }
            is AddPatientAction.OnSelectClient -> {
                _state.update {
                    it.copy(
                        formState = it.formState.copy(selectedClient = action.client)
                    )
                }
            }
            is AddPatientAction.OnOpenAddCatalogSheet -> {
                _state.update {
                    it.copy(
                        isAddCatalogSheetOpen = true,
                        activeCatalogTypeId = action.catalogTypeId,
                        activeCatalogTypeName = action.catalogTypeName
                    )
                }
            }
            AddPatientAction.OnDismissAddCatalogSheet -> {
                _state.update { it.copy(isAddCatalogSheetOpen = false) }
            }
            is AddPatientAction.OnSaveCatalog -> saveCatalog(action.name)
        }
    }

    private fun filterClients(query: String) {
        val normalizedQuery = query.normalize()
        _state.update { state ->
            val filtered = if (normalizedQuery.isBlank()) {
                state.clients
            } else {
                state.clients.filter { client ->
                    client.firstName.normalize().contains(normalizedQuery) ||
                            client.lastName.normalize().contains(normalizedQuery)
                }
            }
            state.copy(filteredClients = filtered)
        }
    }

    private fun getCatalogsAndClients() {
        _state.update { it.copy(isLoadingDataInitial = true) }
        viewModelScope.launch {
            repository.getAppCatalogsByTypes(
                types = listOf(
                    Constants.SPECIES_TYPE_CATALOG,
                    Constants.GENDER_TYPE_CATALOG
                )
            ).onSuccess { catalogs ->
                val speciesCatalog = catalogs.filter { it.catalogTypeId == Constants.SPECIES_TYPE_CATALOG }
                val genderCatalog = catalogs.filter { it.catalogTypeId == Constants.GENDER_TYPE_CATALOG }

                repository.getClients().onSuccess { clients ->
                    val client = clients.find { it.id == clienteId }
                    _state.update { currentState ->
                        currentState.copy(
                            speciesCatalog = speciesCatalog,
                            genderCatalog = genderCatalog,
                            clients = clients,
                            filteredClients = clients,
                            formState = currentState.formState.copy(selectedClient = client)
                        )
                    }

                    if (!patientId.isNullOrBlank()) {
                        loadPatientForEdit(patientId)
                    } else {
                        val defaultSpeciesId = speciesCatalog.find { it.id == Constants.CANINE_SPECIES_CATALOG }?.id ?: 0
                        val defaultGenderId = genderCatalog.find { it.id == Constants.FEMALE_GENDER_CATALOG }?.id ?: 0
                        _state.update { currentState ->
                            currentState.copy(
                                isLoadingDataInitial = false,
                                formState = currentState.formState.copy(
                                    speciesId = defaultSpeciesId,
                                    genderId = defaultGenderId
                                )
                            )
                        }
                    }
                }.onFailure {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(
                        AddPatientEvent.ShowErrorSnackbar("No pudimos obtener a los clientes. Inténtalo de nuevo.")
                    )
                }
            }.onFailure {
                _state.update { it.copy(isLoadingDataInitial = false) }
                _eventChannel.send(
                    AddPatientEvent.ShowErrorSnackbar("No pudimos obtener los catálogos. Inténtalo de nuevo.")
                )
            }
        }
    }

    private fun loadPatientForEdit(id: String) {
        viewModelScope.launch {
            repository.getPatientByIdRoom(id).fold(
                onSuccess = { patient ->
                    if (patient.clientId.isNotBlank()) {
                        fetchClientForPatient(patient)
                    } else {
                        val initialForm = patient.toAddPatientFormState(null)
                        _state.update { currentState ->
                            currentState.copy(
                                currentPatient = patient,
                                formState = initialForm,
                                initialFormState = initialForm,
                                isLoadingDataInitial = false
                            )
                        }
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(
                        AddPatientEvent.ShowErrorSnackbar("Error al cargar la información del paciente.")
                    )
                }
            )
        }
    }

    private suspend fun fetchClientForPatient(patient: PatientModel) {
        repository.getClientByIdRoom(patient.clientId).fold(
            onSuccess = { client ->
                setPatientAndClientState(patient, client)
            },
            onFailure = {
                repository.getClientByIdSupabase(patient.clientId).fold(
                    onSuccess = { client ->
                        setPatientAndClientState(patient, client)
                    },
                    onFailure = {
                        setPatientAndClientState(patient, null)
                        _eventChannel.send(
                            AddPatientEvent.ShowErrorSnackbar("No se pudo recuperar la información del propietario.")
                        )
                    }
                )
            }
        )
    }

    private fun setPatientAndClientState(patient: PatientModel, client: ClientModel?) {
        val initialForm = patient.toAddPatientFormState(client)
        _state.update { currentState ->
            currentState.copy(
                currentPatient = patient,
                formState = initialForm,
                initialFormState = initialForm,
                isLoadingDataInitial = false
            )
        }
    }

    private fun savePatient() {
        val cs = _state.value
        if (!cs.formState.isValid) return

        if (cs.isEditMode) {
            updatePatient()
        } else {
            registerPatient()
        }
    }

    private fun registerPatient() {
        val cs = _state.value
        _state.update { it.copy(isLoadingRegister = true) }
        viewModelScope.launch {
            val patient = cs.formState.toInsertModel()
            repository.insertPatient(patient)
                .onSuccess {
                    val defaultSpeciesId = cs.speciesCatalog.find { it.id == Constants.CANINE_SPECIES_CATALOG }?.id ?: 0
                    val defaultGenderId = cs.genderCatalog.find { it.id == Constants.FEMALE_GENDER_CATALOG }?.id ?: 0
                    val client = cs.clients.find { it.id == clienteId }
                    _state.update { currentState ->
                        currentState.copy(
                            isLoadingRegister = false,
                            formState = AddPatientFormState(
                                speciesId = defaultSpeciesId,
                                genderId = defaultGenderId,
                                selectedClient = client
                            )
                        )
                    }
                    _eventChannel.send(AddPatientEvent.ShowSuccessSnackbar("Paciente registrado correctamente."))
                }
                .onFailure {
                    _state.update { it.copy(isLoadingRegister = false) }
                    _eventChannel.send(AddPatientEvent.ShowErrorSnackbar("No pudimos registrar al paciente. Inténtalo de nuevo."))
                }
        }
    }

    private fun updatePatient() {
        val cs = _state.value
        val currentPatient = cs.currentPatient ?: return
        _state.update { it.copy(isLoadingUpdatePatient = true) }
        viewModelScope.launch {
            val updatedPatient = cs.formState.toUpdateModel(
                patientId = currentPatient.id,
                photoUrl = currentPatient.photoUrl,
                createdAt = currentPatient.createdAt,
                status = currentPatient.status
            )
            repository.updatePatient(updatedPatient)
                .onSuccess {
                    val newForm = cs.formState
                    _state.update { currentState ->
                        currentState.copy(
                            isLoadingUpdatePatient = false,
                            currentPatient = updatedPatient,
                            formState = newForm,
                            initialFormState = newForm
                        )
                    }
                    _eventChannel.send(AddPatientEvent.ShowSuccessSnackbar("Paciente actualizado correctamente."))
                }
                .onFailure {
                    _state.update { it.copy(isLoadingUpdatePatient = false) }
                    _eventChannel.send(AddPatientEvent.ShowErrorSnackbar("No pudimos actualizar la información del paciente. Inténtalo de nuevo."))
                }
        }
    }

    private fun saveCatalog(name: String) {
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
            repository.insertCatalog(newCatalog)
                .onSuccess { insertedCatalog ->
                    _state.update { state ->
                        val updatedSpecies = if (state.activeCatalogTypeId == Constants.SPECIES_TYPE_CATALOG) {
                            state.speciesCatalog + insertedCatalog
                        } else {
                            state.speciesCatalog
                        }
                        val updatedGender = if (state.activeCatalogTypeId == Constants.GENDER_TYPE_CATALOG) {
                            state.genderCatalog + insertedCatalog
                        } else {
                            state.genderCatalog
                        }
                        val updatedFormState = if (state.activeCatalogTypeId == Constants.SPECIES_TYPE_CATALOG) {
                            state.formState.copy(speciesId = insertedCatalog.id)
                        } else {
                            state.formState.copy(genderId = insertedCatalog.id)
                        }
                        state.copy(
                            isLoadingAddCatalog = false,
                            isAddCatalogSheetOpen = false,
                            speciesCatalog = updatedSpecies,
                            genderCatalog = updatedGender,
                            formState = updatedFormState
                        )
                    }
                    _eventChannel.send(AddPatientEvent.ShowSuccessSnackbar("${currentState.activeCatalogTypeName} agregado correctamente."))
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isLoadingAddCatalog = false,
                            isAddCatalogSheetOpen = false
                        )
                    }
                    _eventChannel.send(AddPatientEvent.ShowErrorSnackbar("No se pudo agregar el catálogo. Inténtalo de nuevo."))
                }
        }
    }
}
