package yosel.dev.atti.screens.add_patient.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.PatientModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.add_patient.domain.AddPatientRepository
import java.text.Normalizer
import javax.inject.Inject

@HiltViewModel
class AddPatientViewModel @Inject constructor(
    private val repository: AddPatientRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddPatientState())
    val state: StateFlow<AddPatientState> = _state

    private val _eventChannel = Channel<AddPatientEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        getCatalogs()
    }

    fun onAction(action: AddPatientAction) {
        when (action) {
            AddPatientAction.RegisterPatient -> registerPatient()
            AddPatientAction.TryCatalogsAgain -> getCatalogs()
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
            // --- Sheet Actions ---
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

    private fun getCatalogs() {
        _state.update { it.copy(isLoadingDataInitial = true) }
        viewModelScope.launch {
            repository.getAppCatalogsByTypes(
                types = listOf(
                    Constants.SPECIES_TYPE_CATALOG,
                    Constants.GENDER_TYPE_CATALOG
                )
            ).onSuccess { catalogs ->
                successCatalogsAndGetClients(catalogs = catalogs)
            }.onFailure {
                _state.update { it.copy(isLoadingDataInitial = false) }
                _eventChannel.send(
                    AddPatientEvent.ShowErrorSnackbar(
                        message = "No pudimos obtener los catálogos. Inténtalo de nuevo."
                    )
                )
            }
        }
    }

    private suspend fun successCatalogsAndGetClients(catalogs: List<AppCatalogModel>) {
        val speciesCatalog = catalogs.filter { it.catalogTypeId == Constants.SPECIES_TYPE_CATALOG }
        val genderCatalog = catalogs.filter { it.catalogTypeId == Constants.GENDER_TYPE_CATALOG }
        val canine = speciesCatalog.find { it.id == Constants.CANINE_SPECIES_CATALOG }
        val female = genderCatalog.find { it.id == Constants.FEMALE_GENDER_CATALOG }

        if (canine != null) {
            _state.update { it.copy(formState = it.formState.copy(speciesId = canine.id)) }
        }
        if (female != null) {
            _state.update { it.copy(formState = it.formState.copy(genderId = female.id)) }
        }
        _state.update {
            it.copy(
                speciesCatalog = speciesCatalog,
                genderCatalog = genderCatalog,
            )
        }
        getClients()
    }

    private suspend fun getClients() {
        repository.getClients()
            .onSuccess { clients ->
                _state.update {
                    it.copy(
                        clients = clients,
                        filteredClients = clients,
                        isLoadingDataInitial = false
                    )
                }
            }.onFailure {
                _state.update { it.copy(isLoadingDataInitial = false) }
                _eventChannel.send(
                    AddPatientEvent.ShowErrorSnackbar(
                        message = "No pudimos obtener a los clientes. Inténtalo de nuevo."
                    )
                )
            }
    }

    private fun registerPatient() {
        val form = _state.value.formState
        if (!form.isValid) return
        _state.update { it.copy(isLoadingRegister = true) }
        viewModelScope.launch {
            val patient = PatientModel(
                clientId = form.selectedClient?.id ?: "",
                name = form.name,
                speciesId = form.speciesId,
                genderId = form.genderId,
                breed = form.breed,
                ageYears = form.ageYears.toIntOrNull() ?: 0,
                ageMonths = form.ageMonths.toIntOrNull() ?: 0,
                color = form.color,
                isNeutered = form.isNeutered
            )
            repository.insertPatient(patient)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoadingRegister = false,
                            formState = AddPatientFormState()
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
}
