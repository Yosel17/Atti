package yosel.dev.atti.screens.navigation_bar.inventory.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.navigation_bar.inventory.domain.InventoryRepository
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InventoryState())

    val state: StateFlow<InventoryState> = combine(
        repository.getAllProducts().catch {
            _events.send(InventoryEvent.ShowSnackBarError("Error al obtener los productos locales"))
        },
        repository.getAllServices().catch {
            _events.send(InventoryEvent.ShowSnackBarError("Error al obtener los servicios locales"))
        },
        repository.getAllSuppliers().catch {
            _events.send(InventoryEvent.ShowSnackBarError("Error al obtener los proveedores locales"))
        },
        _state
    ) { products, services, suppliers, localState ->
        val productQueryNormalized = localState.productSearchQuery.normalize()
        val serviceQueryNormalized = localState.serviceSearchQuery.normalize()
        val supplierQueryNormalized = localState.supplierSearchQuery.normalize()

        val filteredProducts = if (productQueryNormalized.isBlank()) {
            products
        } else {
            products.filter { productWithDetails ->
                productWithDetails.product.commercialName.normalize().contains(productQueryNormalized) ||
                        productWithDetails.product.brand.normalize().contains(productQueryNormalized) ||
                        productWithDetails.category.name.normalize().contains(productQueryNormalized) ||
                        productWithDetails.supplier.name.normalize().contains(productQueryNormalized)
            }
        }

        val filteredServices = if (serviceQueryNormalized.isBlank()) {
            services
        } else {
            services.filter { serviceWithDetails ->
                serviceWithDetails.service.name.normalize().contains(serviceQueryNormalized) ||
                        serviceWithDetails.service.description.normalize().contains(serviceQueryNormalized) ||
                        serviceWithDetails.category.name.normalize().contains(serviceQueryNormalized)
            }
        }

        val filteredSuppliers = if (supplierQueryNormalized.isBlank()) {
            suppliers
        } else {
            suppliers.filter { supplier ->
                supplier.name.normalize().contains(supplierQueryNormalized) ||
                        supplier.taxId.normalize().contains(supplierQueryNormalized) ||
                        supplier.phoneNumber.normalize().contains(supplierQueryNormalized)
            }
        }

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