package yosel.dev.atti.screens.top_level.products.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.models.filter.DateSortOrder
import yosel.dev.atti.core.models.model.AppCatalogModel
import yosel.dev.atti.core.models.model.ProductWithDetailsModel
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.top_level.products.domain.ProductsRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsState())
    private val _events = Channel<ProductsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val debouncedQuery = _state
        .map { it.searchQuery }
        .distinctUntilChanged()
        .debounce(300.milliseconds)

    private val filterFlow = _state
        .map { it.filter }
        .distinctUntilChanged()

    private val productsFlow = combine(
        repository.getAllProducts().catch {
            _events.send(ProductsEvent.ShowSnackBarError("Error al cargar productos locales"))
            emit(emptyList())
        },
        debouncedQuery,
        filterFlow
    ) { products, query, filter ->
        val queryNorm = query.normalize()
        val filtered = products
            .filter { item ->
                val p = item.product
                val matchesQuery = queryNorm.isBlank() ||
                        p.commercialName.normalize().contains(queryNorm) ||
                        p.brand.normalize().contains(queryNorm)
                val matchesCategory = filter.categoryId == null || p.categoryId == filter.categoryId
                val matchesUnit = filter.unitTypeId == null || p.unitTypeId == filter.unitTypeId
                val matchesSupplier = filter.supplierId == null || p.supplierId == filter.supplierId
                val matchesStatus = filter.status.statusCode == null || p.status == filter.status.statusCode
                matchesQuery && matchesCategory && matchesUnit && matchesSupplier && matchesStatus
            }
            .let { list ->
                when (filter.dateSort) {
                    DateSortOrder.NEWEST -> list.sortedByDescending { it.product.createdAt }
                    DateSortOrder.OLDEST -> list.sortedBy { it.product.createdAt }
                }
            }

        val categories = products
            .map { it.category }
            .filter { it.id != 0 && it.name.isNotBlank() }
            .distinctBy { it.id }

        val unitTypes = products
            .map { it.unitType }
            .filter { it.id != 0 && it.name.isNotBlank() }
            .distinctBy { it.id }

        val suppliers = products
            .map { it.supplier }
            .filter { it.id.isNotBlank() && it.name.isNotBlank() }
            .distinctBy { it.id }

        CalculatedProducts(products, filtered, categories, unitTypes, suppliers)
    }

    val state: StateFlow<ProductsState> = combine(productsFlow, _state) { calc, localState ->
        localState.copy(
            products = calc.products,
            filteredProducts = calc.filtered,
            availableCategories = calc.categories,
            availableUnitTypes = calc.unitTypes,
            availableSuppliers = calc.suppliers
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProductsState()
    )

    init {
        syncRemoteProducts()
    }

    fun onAction(action: ProductsAction) {
        when (action) {
            is ProductsAction.OnSearchQueryChange -> _state.update { it.copy(searchQuery = action.query) }
            is ProductsAction.OnApplyFilter -> _state.update { it.copy(filter = action.filter) }
            is ProductsAction.OnToggleFilterSheet -> _state.update { it.copy(showFilterSheet = action.isOpen) }
        }
    }

    private fun syncRemoteProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.syncProducts()
                .onSuccess { _state.update { it.copy(isLoading = false) } }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProductsEvent.ShowSnackBarError("Error al sincronizar productos"))
                }
        }
    }

    private data class CalculatedProducts(
        val products: List<ProductWithDetailsModel>,
        val filtered: List<ProductWithDetailsModel>,
        val categories: List<AppCatalogModel>,
        val unitTypes: List<AppCatalogModel>,
        val suppliers: List<SupplierModel>
    )
}