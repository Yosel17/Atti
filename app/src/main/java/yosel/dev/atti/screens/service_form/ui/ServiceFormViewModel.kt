package yosel.dev.atti.screens.service_form.ui

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
import yosel.dev.atti.core.models.model.ProductModel
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.service_form.domain.ServiceFormRepository

@HiltViewModel(assistedFactory = ServiceFormViewModel.Factory::class)
class ServiceFormViewModel @AssistedInject constructor(
    private val repository: ServiceFormRepository,
    @Assisted("serviceId") private val serviceId: String?
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("serviceId") serviceId: String?): ServiceFormViewModel
    }

    private val _state = MutableStateFlow(
        ServiceFormState(
            isEditMode = !serviceId.isNullOrBlank(),
            serviceId = serviceId
        )
    )
    val state: StateFlow<ServiceFormState> = _state

    private val _eventChannel = Channel<ServiceFormEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        getCatalogs()
    }

    fun onAction(action: ServiceFormAction) {
        when (action) {
            ServiceFormAction.TryCatalogsAgain -> getCatalogs()
            is ServiceFormAction.OnChangeValueFormInputState -> {
                changeValueFormInputState(value = action.value, field = action.field)
            }
            ServiceFormAction.OnOpenCategorySheet -> {
                _state.update { it.copy(isCategorySheetOpen = true, categorySearchQuery = "") }
                filterCategory(query = "")
            }
            is ServiceFormAction.OnSearchCategoryQueryChange -> {
                _state.update { it.copy(categorySearchQuery = action.query) }
                filterCategory(query = action.query)
            }
            is ServiceFormAction.OnSelectCategory -> {
                _state.update {
                    it.copy(
                        formInputState = it.formInputState.copy(selectedCategory = action.category)
                    )
                }
            }
            ServiceFormAction.OnDismissCategorySheet -> {
                _state.update { it.copy(isCategorySheetOpen = false) }
            }
            is ServiceFormAction.OnShowAddCatalogDialog -> {
                _state.update {
                    it.copy(
                        activeCatalogTypeId = action.catalogTypeId,
                        activeCatalogTypeName = action.catalogTypeName,
                        showAddAppCatalogDialog = true
                    )
                }
            }
            ServiceFormAction.OnDismissAddAppCatalogDialog -> {
                _state.update {
                    it.copy(
                        showAddAppCatalogDialog = false,
                        activeCatalogTypeId = 0,
                        activeCatalogTypeName = ""
                    )
                }
            }
            is ServiceFormAction.OnSaveAppCatalog -> onSaveAppCatalog(action.name)
            is ServiceFormAction.OnChangeExpenseMode -> {
                _state.update {
                    it.copy(
                        formInputState = it.formInputState.copy(expenseMode = action.mode)
                    )
                }
            }
            ServiceFormAction.OnOpenProductSheet -> handleOpenProductSheet()
            ServiceFormAction.OnDismissProductSheet -> {
                _state.update { it.copy(isProductSheetOpen = false) }
            }
            is ServiceFormAction.OnSearchProductQueryChange -> {
                _state.update { it.copy(productSearchQuery = action.query) }
                filterProducts(query = action.query)
            }
            is ServiceFormAction.OnToggleSelectProduct -> toggleSelectProduct(action.product)
            ServiceFormAction.OnConfirmProductSelection -> confirmProductSelection()
            is ServiceFormAction.OnIncrementProductQuantity -> incrementProductQuantity(action.productId)
            is ServiceFormAction.OnDecrementProductQuantity -> decrementProductQuantity(action.productId)
            is ServiceFormAction.OnRemoveProductSupply -> removeProductSupply(action.productId)
            ServiceFormAction.OnSaveService -> {
                // Validación lista para futura inserción
            }
        }
    }

    private fun getCatalogs() {
        _state.update { it.copy(isLoadingDataInitial = true) }
        viewModelScope.launch {
            repository.getAppCatalogsByTypes(
                types = listOf(Constants.SERVICE_CATEGORY_TYPE_CATALOG)
            ).fold(
                onSuccess = { appCatalogs ->
                    val categories = appCatalogs
                        .filter { it.catalogTypeId == Constants.SERVICE_CATEGORY_TYPE_CATALOG }
                        .sortedBy { it.name.lowercase() }
                    _state.update { currentState ->
                        currentState.copy(
                            categories = categories,
                            filteredCategories = categories,
                            isSuccessGetCategory = true,
                            isLoadingDataInitial = false
                        )
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingDataInitial = false) }
                    _eventChannel.send(
                        ServiceFormEvent.ShowErrorSnackbar("No pudimos obtener los catálogos. Inténtalo de nuevo.")
                    )
                }
            )
        }
    }

    private fun handleOpenProductSheet() {
        val currentState = _state.value
        val currentSelectedIds = currentState.formInputState.selectedProducts.map { it.product.id }.toSet()

        if (currentState.productsWithDetails.isEmpty()) {
            _state.update { it.copy(isLoadingProducts = true) }
            viewModelScope.launch {
                repository.getActiveProductsWithDetails().fold(
                    onSuccess = { productsList ->
                        val sortedProducts = productsList.sortedBy { it.product.commercialName.lowercase() }
                        _state.update { state ->
                            state.copy(
                                isLoadingProducts = false,
                                productsWithDetails = sortedProducts,
                                filteredProductsWithDetails = sortedProducts,
                                productSearchQuery = "",
                                tempSelectedProductIds = currentSelectedIds,
                                isProductSheetOpen = true
                            )
                        }
                    },
                    onFailure = {
                        _state.update { it.copy(isLoadingProducts = false) }
                        _eventChannel.send(
                            ServiceFormEvent.ShowErrorSnackbar("Hubo un error al cargar los productos. Inténtalo de nuevo.")
                        )
                    }
                )
            }
        } else {
            _state.update {
                it.copy(
                    productSearchQuery = "",
                    filteredProductsWithDetails = it.productsWithDetails,
                    tempSelectedProductIds = currentSelectedIds,
                    isProductSheetOpen = true
                )
            }
        }
    }

    private fun filterProducts(query: String) {
        val normalizedQuery = query.normalize()
        _state.update { currentState ->
            val filtered = if (normalizedQuery.isBlank()) {
                currentState.productsWithDetails
            } else {
                currentState.productsWithDetails.filter { productWithDetails ->
                    productWithDetails.product.commercialName.normalize().contains(normalizedQuery) ||
                            productWithDetails.product.brand.normalize().contains(normalizedQuery)
                }
            }
            currentState.copy(filteredProductsWithDetails = filtered)
        }
    }

    private fun toggleSelectProduct(product: ProductModel) {
        _state.update { currentState ->
            val newSelection = if (currentState.tempSelectedProductIds.contains(product.id)) {
                currentState.tempSelectedProductIds - product.id
            } else {
                currentState.tempSelectedProductIds + product.id
            }
            currentState.copy(tempSelectedProductIds = newSelection)
        }
    }

    private fun confirmProductSelection() {
        val currentState = _state.value
        val existingSuppliesMap = currentState.formInputState.selectedProducts.associateBy { it.product.id }

        val newSelectedProducts = currentState.productsWithDetails
            .filter { currentState.tempSelectedProductIds.contains(it.product.id) }
            .map { productWithDetails ->
                existingSuppliesMap[productWithDetails.product.id] ?: SelectedProductSupply(product = productWithDetails.product, quantity = 1.0)
            }

        _state.update {
            it.copy(
                isProductSheetOpen = false,
                formInputState = it.formInputState.copy(selectedProducts = newSelectedProducts)
            )
        }
    }

    private fun incrementProductQuantity(productId: String) {
        _state.update { currentState ->
            val updated = currentState.formInputState.selectedProducts.map { item ->
                if (item.product.id == productId) {
                    item.copy(quantity = item.quantity + 1.0)
                } else item
            }
            currentState.copy(
                formInputState = currentState.formInputState.copy(selectedProducts = updated)
            )
        }
    }

    private fun decrementProductQuantity(productId: String) {
        _state.update { currentState ->
            val updated = currentState.formInputState.selectedProducts.map { item ->
                if (item.product.id == productId && item.quantity > 1.0) {
                    item.copy(quantity = item.quantity - 1.0)
                } else item
            }
            currentState.copy(
                formInputState = currentState.formInputState.copy(selectedProducts = updated)
            )
        }
    }

    private fun removeProductSupply(productId: String) {
        _state.update { currentState ->
            val updated = currentState.formInputState.selectedProducts.filterNot { it.product.id == productId }
            currentState.copy(
                formInputState = currentState.formInputState.copy(selectedProducts = updated)
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
                        Constants.SERVICE_NAME_FIELD -> form.copy(name = value)
                        Constants.SERVICE_SALE_PRICE_FIELD -> form.copy(salePrice = value)
                        Constants.SERVICE_ESTIMATED_COST_FIELD -> form.copy(estimatedCost = value)
                        else -> form
                    }
                }
            )
        }
    }

    private fun filterCategory(query: String) {
        val normalizedQuery = query.normalize()
        _state.update { currentState ->
            val filtered = if (normalizedQuery.isBlank()) {
                currentState.categories
            } else {
                currentState.categories.filter { category ->
                    category.name.normalize().contains(normalizedQuery)
                }
            }
            currentState.copy(filteredCategories = filtered)
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
                            val updatedCategories = (state.categories + insertedCatalog).sortedBy { it.name.lowercase() }
                            state.copy(
                                categories = updatedCategories,
                                filteredCategories = updatedCategories,
                                formInputState = state.formInputState.copy(selectedCategory = insertedCatalog),
                                isLoadingAddCatalog = false,
                                showAddAppCatalogDialog = false
                            )
                        }
                        _eventChannel.send(ServiceFormEvent.ShowToast("${currentState.activeCatalogTypeName} agregado correctamente."))
                    },
                    onFailure = {
                        _state.update {
                            it.copy(
                                isLoadingAddCatalog = false,
                                showAddAppCatalogDialog = false
                            )
                        }
                        _eventChannel.send(ServiceFormEvent.ShowToast("No se pudo agregar el catálogo. Inténtalo de nuevo."))
                    }
                )
        }
    }
}