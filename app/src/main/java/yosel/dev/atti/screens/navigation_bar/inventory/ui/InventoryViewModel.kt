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

    // 1. Flujos con debounce para cada pestaña
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

    // 2. Filtrado de productos
    private val productsFlow = combine(
        repository.getAllProducts().catch {
            _events.send(InventoryEvent.ShowSnackBarError("Error al obtener los productos locales"))
        },
        debouncedProductQuery
    ) { products, query ->
        val productQueryNormalized = query.normalize()
        val filtered = if (productQueryNormalized.isBlank()) {
            products
        } else {
            products.filter { productWithDetails ->
                productWithDetails.product.commercialName.normalize().contains(productQueryNormalized) ||
                        productWithDetails.product.brand.normalize().contains(productQueryNormalized) ||
                        productWithDetails.category.name.normalize().contains(productQueryNormalized) ||
                        productWithDetails.supplier.name.normalize().contains(productQueryNormalized)
            }
        }
        products to filtered
    }

    // 3. Filtrado de servicios
    private val servicesFlow = combine(
        repository.getAllServices().catch {
            _events.send(InventoryEvent.ShowSnackBarError("Error al obtener los servicios locales"))
        },
        debouncedServiceQuery
    ) { services, query ->
        val serviceQueryNormalized = query.normalize()
        val filtered = if (serviceQueryNormalized.isBlank()) {
            services
        } else {
            services.filter { serviceWithDetails ->
                serviceWithDetails.service.name.normalize().contains(serviceQueryNormalized) ||
                        serviceWithDetails.service.description.normalize().contains(serviceQueryNormalized) ||
                        serviceWithDetails.category.name.normalize().contains(serviceQueryNormalized)
            }
        }
        services to filtered
    }

    // 4. Filtrado de proveedores
    private val suppliersFlow = combine(
        repository.getAllSuppliers().catch {
            _events.send(InventoryEvent.ShowSnackBarError("Error al obtener los proveedores locales"))
        },
        debouncedSupplierQuery
    ) { suppliers, query ->
        val supplierQueryNormalized = query.normalize()
        val filtered = if (supplierQueryNormalized.isBlank()) {
            suppliers
        } else {
            suppliers.filter { supplier ->
                supplier.name.normalize().contains(supplierQueryNormalized) ||
                        supplier.taxId.normalize().contains(supplierQueryNormalized) ||
                        supplier.phoneNumber.normalize().contains(supplierQueryNormalized)
            }
        }
        suppliers to filtered
    }

    // 5. Estado unificado para la UI
    val state: StateFlow<InventoryState> = combine(
        productsFlow,
        servicesFlow,
        suppliersFlow,
        _state
    ) { (products, filteredProducts), (services, filteredServices), (suppliers, filteredSuppliers), localState ->
        localState.copy(
            products = products,
            filteredProducts = filteredProducts,
            services = services,
            filteredServices = filteredServices,
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
            is InventoryAction.OnTabSelected -> {
                onTabSelected(index = event.index)
            }
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
}