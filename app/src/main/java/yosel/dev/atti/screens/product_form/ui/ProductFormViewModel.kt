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
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.add_patient.ui.AddPatientEvent
import yosel.dev.atti.screens.product_form.domain.ProductFormRepository
import kotlin.text.contains

@HiltViewModel(assistedFactory = ProductFormViewModel.Factory::class)
class ProductFormViewModel @AssistedInject constructor(
    private val repository: ProductFormRepository,
    @Assisted("productId") private val productId: String?
) : ViewModel() {

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

    fun onAction(action: ProductFormAction) {
        when (action) {
            ProductFormAction.RegisterProduct -> TODO()
            ProductFormAction.TryCatalogsAgain -> getCatalogsAndSuppliers()
            is ProductFormAction.OnChangeValueFormInputState -> {
                changeValueFormInputState(value = action.value, field = action.field)
            }
            ProductFormAction.OnOpenClientSheet -> {
                _state.update { it.copy(isCategorySheetOpen = true, categorySearchQuery = "") }
                filterCategory(query = "")
            }
            is ProductFormAction.OnSearchCategoryQueryChange -> {
                _state.update { it.copy(categorySearchQuery = action.query) }
                filterCategory(query = action.query)
            }
            is ProductFormAction.OnSelectCategory -> {
                _state.update {
                    it.copy(
                        formInputState = it.formInputState.copy(selectedCategory = action.category)
                    )
                }
            }
            ProductFormAction.OnDismissCategorySheet -> {
                _state.update { it.copy(isCategorySheetOpen = false) }
            }
            is ProductFormAction.OnShowAddCatalogDialog -> {
                _state.update {
                    it.copy(
                        activeCatalogTypeId = action.catalogTypeId,
                        activeCatalogTypeName = action.catalogTypeName,
                        showAddAppCatalogDialog = true,
                    )
                }
            }
            ProductFormAction.OnDismissAddAppCatalogDialog -> {
                _state.update {
                    it.copy(
                        showAddAppCatalogDialog = false,
                        activeCatalogTypeId = 0,
                        activeCatalogTypeName = "",
                    )
                }
            }
            is ProductFormAction.OnSaveAppCatalog -> onSaveAppCatalog(action.name)
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
                    val categories =
                        it.filter { it.catalogTypeId == Constants.PRODUCT_CATEGORY_TYPE_CATALOG }
                    val unitsOfMeasurement =
                        it.filter { it.catalogTypeId == Constants.PRODUCT_UNIT_OF_MEASURE_TYPE_CATALOG }

                    repository.getSuppliers().fold(
                        onSuccess = { suppliers ->
                            _state.update { currentState ->
                                currentState.copy(
                                    categories = categories,
                                    unitsOfMeasurement = unitsOfMeasurement,
                                    suppliers = suppliers,
                                    filteredSuppliers = suppliers,
                                    isSuccessGetCategory = true,
                                    isSuccessGetSuppliers = true,
                                    isLoadingDataInitial = false,
                                )
                            }
                        },
                        onFailure = {
                            _state.update { it.copy(isLoadingDataInitial = false) }
                            _eventChannel.send(
                                ProductFormEvent.ShowErrorSnackbar("No pudimos obtener a los proveedores. Inténtalo de nuevo.")
                            )
                        }
                    )
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

    private fun changeValueFormInputState(value: String, field: Int) {
        _state.update {
            it.copy(
                formInputState = it.formInputState.copy(
                    touchedFields = it.formInputState.touchedFields + field
                ).let { form ->
                    when (field) {
                        Constants.PRODUCT_COMMERCIAL_NAME_FIELD -> form.copy(commercialName = value)
                        Constants.PRODUCT_BRAND_FIELD -> form.copy(brand = value)
                        Constants.PRODUCT_PURCHASE_PRICE_FIELD -> form.copy(purchasePrice = value)
                        Constants.PRODUCT_SALE_PRICE_FIELD -> form.copy(salePrice = value)
                        Constants.PRODUCT_STOCK_FIELD -> form.copy(stock = value)
                        Constants.PRODUCT_MIN_STOCK_FIELD -> form.copy(minStock = value)
                        else -> form
                    }
                }
            )
        }
    }

    private fun filterCategory(query: String){
        val normalizedQuery = query.normalize()
        _state.update { state ->
            val filtered = if (normalizedQuery.isBlank()) {
                state.categories
            } else {
                state.categories.filter { category ->
                    category.name.normalize().contains(normalizedQuery)
                }
            }
            state.copy(filteredCategories = filtered)
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
            repository.insertCatalog(catalog = newCatalog)
                .fold(
                    onSuccess = { insertedCatalog ->
                        _state.update { state ->
                            val updatedCategories = if (currentState.activeCatalogTypeId == Constants.PRODUCT_CATEGORY_TYPE_CATALOG) {
                                state.categories + insertedCatalog
                            }else{
                                state.categories
                            }
                            val updatedUnitsOfMeasurement = if (currentState.activeCatalogTypeId == Constants.PRODUCT_UNIT_OF_MEASURE_TYPE_CATALOG) {
                                state.unitsOfMeasurement + insertedCatalog
                            }else{
                                state.unitsOfMeasurement
                            }
                            val updateFormInputsState = if (currentState.activeCatalogTypeId == Constants.PRODUCT_CATEGORY_TYPE_CATALOG) {
                                state.formInputState.copy(selectedCategory = insertedCatalog)
                            }else{
                                state.formInputState.copy(selectedUnitType = insertedCatalog)
                            }
                            state.copy(
                                categories = updatedCategories,
                                unitsOfMeasurement = updatedUnitsOfMeasurement,
                                formInputState = updateFormInputsState,
                                isLoadingAddCatalog = false,
                                showAddAppCatalogDialog = false,
                            )
                        }
                        _eventChannel.send(ProductFormEvent.ShowSuccessSnackbar("${currentState.activeCatalogTypeName} agregado correctamente."))
                    },
                    onFailure = {
                        _state.update {
                            it.copy(
                                isLoadingAddCatalog = false,
                                showAddAppCatalogDialog = false
                            )
                        }
                        _eventChannel.send(ProductFormEvent.ShowErrorSnackbar("No se pudo agregar el catálogo. Inténtalo de nuevo."))
                    }
                )
        }
    }
}