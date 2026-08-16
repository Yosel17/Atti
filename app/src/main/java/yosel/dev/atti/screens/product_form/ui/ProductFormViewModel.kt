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
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.core.utils.toInsertModel
import yosel.dev.atti.core.utils.toProductFormInputsState
import yosel.dev.atti.core.utils.toUpdateModel
import yosel.dev.atti.screens.product_form.domain.ProductFormRepository

@HiltViewModel(assistedFactory = ProductFormViewModel.Factory::class)
class ProductFormViewModel @AssistedInject constructor(
    private val repository: ProductFormRepository,
    @Assisted("productId") private val productId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("productId") productId: String?): ProductFormViewModel
    }

    private val _state = MutableStateFlow(
        ProductFormState(
            isEditMode = !productId.isNullOrBlank(),
            productId = productId
        )
    )
    val state: StateFlow<ProductFormState> = _state

    private val _eventChannel = Channel<ProductFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        getCatalogsAndSuppliers()
    }

    fun onAction(action: ProductFormAction) {
        when (action) {
            ProductFormAction.RegisterProduct -> saveProduct()
            ProductFormAction.TryCatalogsAgain -> getCatalogsAndSuppliers()
            is ProductFormAction.OnChangeValueFormInputState -> {
                changeValueFormInputState(value = action.value, field = action.field)
            }
            ProductFormAction.OnOpenCategorySheet -> {
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
            ProductFormAction.OnOpenUnitsMeasurementSheet -> {
                _state.update {
                    it.copy(isUnitsOfMeasurementSheetOpen = true, unitsOfMeasurementSearchQuery = "")
                }
                filterUnitsOfMeasurement(query = "")
            }
            ProductFormAction.OnDismissUnitsMeasurementSheet -> {
                _state.update { it.copy(isUnitsOfMeasurementSheetOpen = false) }
            }
            is ProductFormAction.OnSearchUnitsMeasurementQueryChange -> {
                _state.update { it.copy(unitsOfMeasurementSearchQuery = action.query) }
                filterUnitsOfMeasurement(query = action.query)
            }
            is ProductFormAction.OnSelectUnitsMeasurement -> {
                _state.update {
                    it.copy(
                        formInputState = it.formInputState.copy(
                            selectedUnitType = action.unitsOfMeasurement
                        )
                    )
                }
            }
            ProductFormAction.OnOpenSupplierSheet -> {
                _state.update { it.copy(isSupplierSheetOpen = true, supplierSearchQuery = "") }
                filterSupplier(query = "")
            }
            ProductFormAction.OnDismissSupplierSheet -> {
                _state.update { it.copy(isSupplierSheetOpen = false) }
            }
            is ProductFormAction.OnSearchSupplierQueryChange -> {
                _state.update { it.copy(supplierSearchQuery = action.query) }
                filterSupplier(query = action.query)
            }
            is ProductFormAction.OnSelectSupplier -> {
                _state.update {
                    it.copy(
                        formInputState = it.formInputState.copy(
                            selectedSupplier = action.supplier
                        )
                    )
                }
            }
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
                onSuccess = { appCatalogs ->
                    val categories = appCatalogs
                        .filter { it.catalogTypeId == Constants.PRODUCT_CATEGORY_TYPE_CATALOG }
                        .sortedBy { it.name.lowercase() }
                    val unitsOfMeasurement = appCatalogs
                        .filter { it.catalogTypeId == Constants.PRODUCT_UNIT_OF_MEASURE_TYPE_CATALOG }
                        .sortedBy { it.name.lowercase() }

                    repository.getSuppliers().fold(
                        onSuccess = { suppliers ->
                            _state.update { currentState ->
                                currentState.copy(
                                    categories = categories,
                                    filteredCategories = categories,
                                    unitsOfMeasurement = unitsOfMeasurement,
                                    filteredUnitsOfMeasurement = unitsOfMeasurement,
                                    suppliers = suppliers,
                                    filteredSuppliers = suppliers,
                                    isSuccessGetCategory = true,
                                    isSuccessGetSuppliers = true,
                                )
                            }
                            if (!productId.isNullOrBlank()) {
                                loadProductForEdit(
                                    id = productId,
                                    categories = categories,
                                    unitsOfMeasurement = unitsOfMeasurement,
                                    suppliers = suppliers
                                )
                            } else {
                                _state.update { it.copy(isLoadingDataInitial = false) }
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

    private fun loadProductForEdit(
        id: String,
        categories: List<AppCatalogModel>,
        unitsOfMeasurement: List<AppCatalogModel>,
        suppliers: List<SupplierModel>
    ) {
        viewModelScope.launch {
            repository.getProductByIdRoom(id).fold(
                onSuccess = { product ->
                    val category = categories.find { it.id == product.categoryId }
                    val unitType = unitsOfMeasurement.find { it.id == product.unitTypeId }
                    val supplier = suppliers.find { it.id == product.supplierId }
                    val initialForm = product.toProductFormInputsState(
                        category = category,
                        unitType = unitType,
                        supplier = supplier
                    )
                    _state.update { currentState ->
                        currentState.copy(
                            currentProduct = product,
                            formInputState = initialForm,
                            initialFormInputState = initialForm,
                            isLoadingDataInitial = false
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(
                        ProductFormEvent.ShowErrorSnackbar("Error al cargar la información del producto.")
                    )
                }
            )
        }
    }

    private fun saveProduct() {
        val cs = _state.value
        if (!cs.formInputState.isValid) return
        if (cs.isEditMode) {
            updateProduct()
        } else {
            registerProduct()
        }
    }

    private fun registerProduct() {
        val cs = _state.value
        _state.update { it.copy(isLoadingRegisterProduct = true) }
        viewModelScope.launch {
            val product = cs.formInputState.toInsertModel()
            repository.insertProduct(product = product)
                .fold(
                    onSuccess = {
                        _state.update {
                            it.copy(
                                formInputState = ProductFormInputsState(),
                                isLoadingRegisterProduct = false
                            )
                        }
                        _eventChannel.send(ProductFormEvent.ShowSuccessSnackbar("Producto registrado correctamente."))
                    },
                    onFailure = {
                        _state.update { it.copy(isLoadingRegisterProduct = false) }
                        _eventChannel.send(ProductFormEvent.ShowErrorSnackbar("No pudimos registrar el producto. Inténtalo de nuevo."))
                    }
                )
        }
    }

    private fun updateProduct() {
        val cs = _state.value
        val currentProduct = cs.currentProduct ?: return
        _state.update { it.copy(isLoadingUpdateProduct = true) }
        viewModelScope.launch {
            val updatedProduct = cs.formInputState.toUpdateModel(
                productId = currentProduct.id,
                createdAt = currentProduct.createdAt,
                status = currentProduct.status
            )
            repository.updateProduct(product = updatedProduct)
                .fold(
                    onSuccess = {
                        val newForm = cs.formInputState
                        _state.update { currentState ->
                            currentState.copy(
                                isLoadingUpdateProduct = false,
                                currentProduct = updatedProduct,
                                formInputState = newForm,
                                initialFormInputState = newForm
                            )
                        }
                        _eventChannel.send(ProductFormEvent.ShowSuccessSnackbar("Producto actualizado correctamente."))
                    },
                    onFailure = {
                        _state.update { it.copy(isLoadingUpdateProduct = false) }
                        _eventChannel.send(ProductFormEvent.ShowErrorSnackbar("No pudimos actualizar el producto. Inténtalo de nuevo."))
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

    private fun filterCategory(query: String) {
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
                                (state.categories + insertedCatalog).sortedBy { it.name.lowercase() }
                            } else {
                                state.categories
                            }
                            val updatedUnitsOfMeasurement = if (currentState.activeCatalogTypeId == Constants.PRODUCT_UNIT_OF_MEASURE_TYPE_CATALOG) {
                                (state.unitsOfMeasurement + insertedCatalog).sortedBy { it.name.lowercase() }
                            } else {
                                state.unitsOfMeasurement
                            }
                            val updateFormInputsState = if (currentState.activeCatalogTypeId == Constants.PRODUCT_CATEGORY_TYPE_CATALOG) {
                                state.formInputState.copy(selectedCategory = insertedCatalog)
                            } else {
                                state.formInputState.copy(selectedUnitType = insertedCatalog)
                            }
                            state.copy(
                                categories = updatedCategories,
                                unitsOfMeasurement = updatedUnitsOfMeasurement,
                                filteredCategories = updatedCategories,
                                filteredUnitsOfMeasurement = updatedUnitsOfMeasurement,
                                formInputState = updateFormInputsState,
                                isLoadingAddCatalog = false,
                                showAddAppCatalogDialog = false,
                            )
                        }
                        _eventChannel.send(ProductFormEvent.ShowToast("${currentState.activeCatalogTypeName} agregado correctamente."))
                    },
                    onFailure = {
                        _state.update {
                            it.copy(
                                isLoadingAddCatalog = false,
                                showAddAppCatalogDialog = false
                            )
                        }
                        _eventChannel.send(ProductFormEvent.ShowToast("No se pudo agregar el catálogo. Inténtalo de nuevo."))
                    }
                )
        }
    }

    private fun filterUnitsOfMeasurement(query: String) {
        val normalizedQuery = query.normalize()
        _state.update { state ->
            val filtered = if (normalizedQuery.isBlank()) {
                state.unitsOfMeasurement
            } else {
                state.unitsOfMeasurement.filter { category ->
                    category.name.normalize().contains(normalizedQuery)
                }
            }
            state.copy(filteredUnitsOfMeasurement = filtered)
        }
    }

    private fun filterSupplier(query: String) {
        val normalizedQuery = query.normalize()
        _state.update { state ->
            val filtered = if (normalizedQuery.isBlank()) {
                state.suppliers
            } else {
                state.suppliers.filter { category ->
                    category.name.normalize().contains(normalizedQuery)
                }
            }
            state.copy(filteredSuppliers = filtered)
        }
    }
}