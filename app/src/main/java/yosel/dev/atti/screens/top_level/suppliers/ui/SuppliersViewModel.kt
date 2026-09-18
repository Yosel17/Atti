package yosel.dev.atti.screens.top_level.suppliers.ui

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
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.top_level.suppliers.domain.SuppliersRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class SuppliersViewModel @Inject constructor(
    private val repository: SuppliersRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SuppliersState())
    private val _events = Channel<SuppliersEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val debouncedQuery = _state
        .map { it.searchQuery }
        .distinctUntilChanged()
        .debounce(300.milliseconds)

    private val filterFlow = _state
        .map { it.filter }
        .distinctUntilChanged()

    private val suppliersFlow = combine(
        repository.getAllSuppliers().catch {
            _events.send(SuppliersEvent.ShowSnackBarError("Error al cargar proveedores locales"))
            emit(emptyList())
        },
        debouncedQuery,
        filterFlow
    ) { suppliers, query, filter ->
        val queryNorm = query.normalize()
        val filtered = suppliers
            .filter { supplier ->
                val matchesQuery = queryNorm.isBlank() ||
                        supplier.name.normalize().contains(queryNorm) ||
                        supplier.taxId.normalize().contains(queryNorm) ||
                        supplier.phoneNumber.normalize().contains(queryNorm)
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

    val state: StateFlow<SuppliersState> = combine(suppliersFlow, _state) { (suppliers, filtered), localState ->
        localState.copy(
            suppliers = suppliers,
            filteredSuppliers = filtered
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SuppliersState()
    )

    init {
        syncRemoteSuppliers()
    }

    fun onAction(action: SuppliersAction) {
        when (action) {
            is SuppliersAction.OnSearchQueryChange -> _state.update { it.copy(searchQuery = action.query) }
            is SuppliersAction.OnApplyFilter -> _state.update { it.copy(filter = action.filter) }
            is SuppliersAction.OnToggleFilterSheet -> _state.update { it.copy(showFilterSheet = action.isOpen) }
            is SuppliersAction.OnCallClick -> {
                viewModelScope.launch {
                    _events.send(SuppliersEvent.NavigateToPhone(action.phoneNumber))
                }
            }
            is SuppliersAction.OnWhatsappClick -> {
                viewModelScope.launch {
                    _events.send(SuppliersEvent.NavigateToWhatsapp(action.phoneNumber))
                }
            }
        }
    }

    private fun syncRemoteSuppliers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.syncSuppliers()
                .onSuccess { _state.update { it.copy(isLoading = false) } }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SuppliersEvent.ShowSnackBarError("Error al sincronizar proveedores"))
                }
        }
    }
}