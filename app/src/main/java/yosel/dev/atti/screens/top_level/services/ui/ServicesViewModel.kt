package yosel.dev.atti.screens.top_level.services.ui

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
import yosel.dev.atti.core.models.model.ServiceWithDetailsModel
import yosel.dev.atti.core.utils.normalize
import yosel.dev.atti.screens.top_level.services.domain.ServicesRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val repository: ServicesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ServicesState())
    private val _events = Channel<ServicesEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val debouncedQuery = _state
        .map { it.searchQuery }
        .distinctUntilChanged()
        .debounce(300.milliseconds)

    private val filterFlow = _state
        .map { it.filter }
        .distinctUntilChanged()

    private val servicesFlow = combine(
        repository.getAllServices().catch {
            _events.send(ServicesEvent.ShowSnackBarError("Error al cargar servicios locales"))
            emit(emptyList())
        },
        debouncedQuery,
        filterFlow
    ) { services, query, filter ->
        val queryNorm = query.normalize()
        val filtered = services
            .filter { item ->
                val s = item.service
                val matchesQuery = queryNorm.isBlank() || s.name.normalize().contains(queryNorm)
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

        CalculatedServices(services, filtered, categories)
    }

    val state: StateFlow<ServicesState> = combine(servicesFlow, _state) { calc, localState ->
        localState.copy(
            services = calc.services,
            filteredServices = calc.filtered,
            availableCategories = calc.categories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ServicesState()
    )

    init {
        syncRemoteServices()
    }

    fun onAction(action: ServicesAction) {
        when (action) {
            is ServicesAction.OnSearchQueryChange -> _state.update { it.copy(searchQuery = action.query) }
            is ServicesAction.OnApplyFilter -> _state.update { it.copy(filter = action.filter) }
            is ServicesAction.OnToggleFilterSheet -> _state.update { it.copy(showFilterSheet = action.isOpen) }
        }
    }

    private fun syncRemoteServices() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.syncServices()
                .onSuccess { _state.update { it.copy(isLoading = false) } }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ServicesEvent.ShowSnackBarError("Error al sincronizar servicios"))
                }
        }
    }

    private data class CalculatedServices(
        val services: List<ServiceWithDetailsModel>,
        val filtered: List<ServiceWithDetailsModel>,
        val categories: List<AppCatalogModel>
    )
}