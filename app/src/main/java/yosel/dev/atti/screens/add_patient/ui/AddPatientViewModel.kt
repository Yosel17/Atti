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
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.add_patient.domain.AddPatientRepository
import javax.inject.Inject

@HiltViewModel
class AddPatientViewModel @Inject constructor(
    private val repository: AddPatientRepository
): ViewModel() {

    private val _state = MutableStateFlow(AddPatientState())
    val state: StateFlow<AddPatientState> = _state

    private val _eventChannel = Channel<AddPatientEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        getCatalogs()
    }

    fun onAction(action: AddPatientAction){
        when(action){
            else -> {}
        }
    }

    private fun getCatalogs() {
        viewModelScope.launch {
            repository.getAppCatalogsByTypes(
                types = listOf(
                    Constants.SPECIES_TYPE_CATALOG,
                    Constants.GENDER_TYPE_CATALOG
                )
            ).onSuccess { catalogs ->
                successCatalogs(catalogs = catalogs)
            }.onFailure{
                _state.update { it.copy(isLoadingCatalogs = false) }
                _eventChannel.send(
                    AddPatientEvent.ShowErrorSnackbar(
                        message = "No pudimos obtener los catalogos. Inténtalo de nuevo."
                    )
                )
            }
        }
    }

    private fun successCatalogs(catalogs: List<AppCatalogModel>) {
        val speciesCatalog = catalogs.filter { it.catalogTypeId == Constants.SPECIES_TYPE_CATALOG }
        val genderCatalog = catalogs.filter { it.catalogTypeId == Constants.GENDER_TYPE_CATALOG }

        _state.update {
            it.copy(
                speciesCatalog = speciesCatalog,
                genderCatalog = genderCatalog,
                isLoadingCatalogs = false
            )
        }
    }
}