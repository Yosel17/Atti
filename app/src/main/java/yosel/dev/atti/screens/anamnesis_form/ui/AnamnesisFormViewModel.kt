package yosel.dev.atti.screens.anamnesis_form.ui

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
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.anamnesis_form.domain.AnamnesisFormRepository
import javax.inject.Inject


@HiltViewModel
class AnamnesisFormViewModel @Inject constructor(
    private val repository: AnamnesisFormRepository
): ViewModel() {

    private val _state = MutableStateFlow(AnamnesisFormState())
    val state: StateFlow<AnamnesisFormState> = _state

    private val _eventChannel = Channel<AnamnesisFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        getCatalogs()
    }

    private fun getCatalogs() {
        _state.update { it.copy(isLoadingDataInitial = true) }

        viewModelScope.launch {
            repository.getAppCatalogsByTypes(types =
                listOf(
                    Constants.ANIMAL_LIFESTYLE_TYPE_CATALOG,
                    Constants.VACCINE_NAME_TYPE_CATALOG,
                    Constants.VACCINATION_SCHEDULE_TYPE_CATALOG,
                    Constants.INTERNAL_DEWORMER_TYPE_CATALOG,
                    Constants.EXTERNAL_DEWORMER_TYPE_CATALOG,
                    Constants.CONCENTRATE_BRAND_TYPE_CATALOG,
                    Constants.CONCENTRATE_UNIT_OF_MEASURE_TYPE_CATALOG,
                )
            ).fold(
                onSuccess = { appCatalogs ->
                    successGetCatalogs(appCatalogs)
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(
                        AnamnesisFormEvent.ShowErrorSnackbar("No pudimos obtener los catálogos. Inténtalo de nuevo.")
                    )
                }
            )
        }
    }

    private fun successGetCatalogs(appCatalogs: List<AppCatalogModel>) {
        val animalLifestyles = appCatalogs
            .filter { it.catalogTypeId == Constants.ANIMAL_LIFESTYLE_TYPE_CATALOG }
            .sortedBy { it.name.lowercase() }

        val vaccineNames = appCatalogs
            .filter { it.catalogTypeId == Constants.VACCINE_NAME_TYPE_CATALOG }
            .sortedBy { it.name.lowercase() }

        val vaccinationSchedules = appCatalogs
            .filter { it.catalogTypeId == Constants.VACCINATION_SCHEDULE_TYPE_CATALOG }
            .sortedBy { it.name.lowercase() }

        val internalDewormers = appCatalogs
            .filter { it.catalogTypeId == Constants.INTERNAL_DEWORMER_TYPE_CATALOG }
            .sortedBy { it.name.lowercase() }

        val externalDewormers = appCatalogs
            .filter { it.catalogTypeId == Constants.EXTERNAL_DEWORMER_TYPE_CATALOG }
            .sortedBy { it.name.lowercase() }

        val concentrateBrands = appCatalogs
            .filter { it.catalogTypeId == Constants.CONCENTRATE_BRAND_TYPE_CATALOG }
            .sortedBy { it.name.lowercase() }

        val concentrateUnitsOfMeasurement = appCatalogs
            .filter { it.catalogTypeId == Constants.CONCENTRATE_UNIT_OF_MEASURE_TYPE_CATALOG }
            .sortedBy { it.name.lowercase() }

        _state.update { currentState ->
            currentState.copy(
                animalLifestyles = animalLifestyles,
                vaccineNames = vaccineNames,
                vaccinationSchedules = vaccinationSchedules,
                internalDewormers = internalDewormers,
                externalDewormers = externalDewormers,
                concentrateBrands = concentrateBrands,
                concentrateUnitsOfMeasurement = concentrateUnitsOfMeasurement,
                isLoadingDataInitial = false
            )
        }
    }
}