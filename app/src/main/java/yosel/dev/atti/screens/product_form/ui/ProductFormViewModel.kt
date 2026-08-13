package yosel.dev.atti.screens.product_form.ui

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
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.add_patient.ui.AddPatientEvent
import yosel.dev.atti.screens.product_form.domain.ProductFormRepository

@HiltViewModel(assistedFactory = ProductFormViewModel.Factory::class)
class ProductFormViewModel @AssistedInject constructor(
    private val repository: ProductFormRepository,
    @Assisted("productId") private val productId: String?
): ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("productId") productId: String?): ProductFormViewModel
    }

    private val _state = MutableStateFlow(ProductFormState())
    val state: StateFlow<ProductFormState> = _state

    private val _eventChannel = Channel<ProductFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        getCatalogsAndSuppliers()
    }

    fun onAction(action: ProductFormAction){
        when(action){
            ProductFormAction.RegisterProduct -> TODO()
            ProductFormAction.TryCatalogsAgain -> getCatalogsAndSuppliers()
        }

    }

    private fun getCatalogsAndSuppliers() {
        _state.update { it.copy(isLoadingDataInitial = true) }

        viewModelScope.launch {
            repository.getAppCatalogsByTypes(
                types = listOf(
                    Constants.PRODUCT_CATEGORY_TYPE_CATALOG,
                    Constants.PRODUCT_UNIT_OF_MEASURE_TYPE_CATALOG
                )
            ).fold(
                onSuccess = {
                    val productCategoryCatalog = it.filter { it.catalogTypeId == Constants.PRODUCT_CATEGORY_TYPE_CATALOG }
                    val productUnitOfMeasureCatalog = it.filter { it.catalogTypeId == Constants.PRODUCT_UNIT_OF_MEASURE_TYPE_CATALOG }

                    _state.update { currentState ->
                        currentState.copy(
                            productCategoryCatalog = productCategoryCatalog,
                            productUnitOfMeasureCatalog = productUnitOfMeasureCatalog,
                            isLoadingDataInitial = false,
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(
                        ProductFormEvent.ShowErrorSnackbar("No pudimos obtener los catálogos. Inténtalo de nuevo.")
                    )
                }
            )
        }
    }
}