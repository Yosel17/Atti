package yosel.dev.atti.screens.navigation_bar.inventory.ui

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
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.models.model.SupplierModel
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.navigation_bar.inventory.domain.InventoryRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InventoryState())

    private val debouncedProductQuery = _state
        .map { it.productSearchQuery }
        .distinctUntilChanged()
        .debounce(300L.milliseconds)

    private val debouncedServiceQuery = _state
        .map { it.serviceSearchQuery }
        .distinctUntilChanged()
        .debounce(300L.milliseconds)

    private val debouncedSupplierQuery = _state
        .map { it.supplierSearchQuery }
        .distinctUntilChanged()
        .debounce(300L.milliseconds)

    private val productFilterFlow = _state
        .map { it.productFilter }
        .distinctUntilChanged()

    private val serviceFilterFlow = _state
        .map { it.serviceFilter }
        .distinctUntilChanged()

    private val supplierFilterFlow = _state
        .map { it.supplierFilter }
        .distinctUntilChanged()

    // 1. Filtrado de productos y extracción de catálogos
    private val productsFlow = combine(
        repository.getAllProducts().catch {
            _events.send(InventoryEvent.ShowSnackBarError("Error al obtener los productos locales"))
        },
        debouncedProductQuery,
        productFilterFlow
    ) { products, query, filter ->
        val productQueryNormalized = query.normalize()
        val filtered = products
            .filter { productWithDetails ->
                val p = productWithDetails.product
                val matchesQuery = productQueryNormalized.isBlank() ||
                        p.commercialName.normalize().contains(productQueryNormalized) ||
                        p.brand.normalize().contains(productQueryNormalized)

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

        ProductsCalculation(products, filtered, categories, unitTypes, suppliers)
    }

    // 2. Filtrado de servicios y extracción de categorías
    private val servicesFlow = combine(
        repository.getAllServices().catch {
            _events.send(InventoryEvent.ShowSnackBarError("Error al obtener los servicios locales"))
        },
        debouncedServiceQuery,
        serviceFilterFlow
    ) { services, query, filter ->
        val serviceQueryNormalized = query.normalize()
        val filtered = services
            .filter { serviceWithDetails ->
                val s = serviceWithDetails.service
                val matchesQuery = serviceQueryNormalized.isBlank() ||
                        s.name.normalize().contains(serviceQueryNormalized)

                val matchesCategory = filter.categoryId == null || s.categoryId == filter.categoryId
                val matchesStatus = filter.status.statusCode == null || s.status == filter.status.statusCode

                matchesQuery && matchesCategory && matchesStatus
            }
            .let { list ->
                when (filter.dateSort) {
                    DateSortOrder.NEWEST -> list.sortedByDescending { it.service.createdAt }
                    DateSortOrder.OLDEST -> list.sortedBy { it.service.createdAt }
                }
            }

        val categories = services
            .map { it.category }
            .filter { it.id != 0 && it.name.isNotBlank() }
            .distinctBy { it.id }

        ServicesCalculation(services, filtered, categories)
    }

    // 3. Filtrado de proveedores
    private val suppliersFlow = combine(
        repository.getAllSuppliers().catch {
            _events.send(InventoryEvent.ShowSnackBarError("Error al obtener los proveedores locales"))
        },
        debouncedSupplierQuery,
        supplierFilterFlow
    ) { suppliers, query, filter ->
        val supplierQueryNormalized = query.normalize()
        val filtered = suppliers
            .filter { supplier ->
                val matchesQuery = supplierQueryNormalized.isBlank() ||
                        supplier.name.normalize().contains(supplierQueryNormalized) ||
                        supplier.taxId.normalize().contains(supplierQueryNormalized) ||
                        supplier.phoneNumber.normalize().contains(supplierQueryNormalized)

                val matchesStatus = filter.status.statusCode == null || supplier.status == filter.status.statusCode

                matchesQuery && matchesStatus
            }
            .let { list ->
                when (filter.dateSort) {
                    DateSortOrder.NEWEST -> list.sortedByDescending { it.createdAt }
                    DateSortOrder.OLDEST -> list.sortedBy { it.createdAt }
                }
            }
        suppliers to filtered
    }

    val state: StateFlow<InventoryState> = combine(
        productsFlow,
        servicesFlow,
        suppliersFlow,
        _state
    ) { prodCalc, servCalc, (suppliers, filteredSuppliers), localState ->
        localState.copy(
            products = prodCalc.products,
            filteredProducts = prodCalc.filtered,
            productCategories = prodCalc.categories,
            productUnitTypes = prodCalc.unitTypes,
            productSuppliers = prodCalc.suppliers,
            services = servCalc.services,
            filteredServices = servCalc.filtered,
            serviceCategories = servCalc.categories,
            suppliers = suppliers,
            filteredSuppliers = filteredSuppliers
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InventoryState()
    )

    private val _events = Channel<InventoryEvent>()
    val events = _events.receiveAsFlow()

    init {
        fetchRemoteProductsIfNeeded()
    }

    fun onAction(event: InventoryAction) {
        when (event) {
            is InventoryAction.OnTabSelected -> onTabSelected(index = event.index)
            is InventoryAction.OnProductSearchQueryChange -> {
                _state.update { it.copy(productSearchQuery = event.query) }
            }
            is InventoryAction.OnServiceSearchQueryChange -> {
                _state.update { it.copy(serviceSearchQuery = event.query) }
            }
            is InventoryAction.OnSupplierSearchQueryChange -> {
                _state.update { it.copy(supplierSearchQuery = event.query) }
            }
            is InventoryAction.OnCallClick -> {
                viewModelScope.launch {
                    _events.send(InventoryEvent.NavigateToPhone(phoneNumber = event.phoneNumber))
                }
            }
            is InventoryAction.OnWhatsappClick -> {
                viewModelScope.launch {
                    _events.send(InventoryEvent.NavigateToWhatsapp(phoneNumber = event.phoneNumber))
                }
            }
            is InventoryAction.OnApplyProductFilter -> {
                _state.update { it.copy(productFilter = event.filter) }
            }
            is InventoryAction.OnApplyServiceFilter -> {
                _state.update { it.copy(serviceFilter = event.filter) }
            }
            is InventoryAction.OnApplySupplierFilter -> {
                _state.update { it.copy(supplierFilter = event.filter) }
            }
            is InventoryAction.OnToggleProductFilterSheet -> {
                _state.update { it.copy(showProductFilterSheet = event.isOpen) }
            }
            is InventoryAction.OnToggleServiceFilterSheet -> {
                _state.update { it.copy(showServiceFilterSheet = event.isOpen) }
            }
            is InventoryAction.OnToggleSupplierFilterSheet -> {
                _state.update { it.copy(showSupplierFilterSheet = event.isOpen) }
            }
        }
    }

    private fun onTabSelected(index: Int) {
        _state.update { it.copy(selectedTabIndex = index) }
        when (index) {
            1 -> if (_state.value.isFirstServices) fetchRemoteServicesIfNeeded()
            2 -> if (_state.value.isFirstSuppliers) fetchRemoteSuppliersIfNeeded()
        }
    }

    private fun fetchRemoteProductsIfNeeded() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingProducts = true) }
            repository.syncProducts()
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoadingProducts = false,
                            isFirstProducts = false
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isLoadingProducts = false,
                            isFirstProducts = false
                        )
                    }
                    _events.send(
                        InventoryEvent.ShowSnackBarError("Error al sincronizar los productos")
                    )
                }
        }
    }

    private fun fetchRemoteServicesIfNeeded() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingServices = true) }
            repository.syncServices()
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoadingServices = false,
                            isFirstServices = false
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isLoadingServices = false,
                            isFirstServices = false
                        )
                    }
                    _events.send(
                        InventoryEvent.ShowSnackBarError("Error al sincronizar los servicios")
                    )
                }
        }
    }

    private fun fetchRemoteSuppliersIfNeeded() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingSuppliers = true) }
            repository.syncSuppliers()
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoadingSuppliers = false,
                            isFirstSuppliers = false
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isLoadingSuppliers = false,
                            isFirstSuppliers = false
                        )
                    }
                    _events.send(
                        InventoryEvent.ShowSnackBarError("Error al sincronizar los proveedores")
                    )
                }
        }
    }

    private data class ProductsCalculation(
        val products: List<ProductWithDetailsModel>,
        val filtered: List<ProductWithDetailsModel>,
        val categories: List<AppCatalogModel>,
        val unitTypes: List<AppCatalogModel>,
        val suppliers: List<SupplierModel>
    )

    private data class ServicesCalculation(
        val services: List<ServiceWithDetailsModel>,
        val filtered: List<ServiceWithDetailsModel>,
        val categories: List<AppCatalogModel>
    )
}