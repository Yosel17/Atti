package yosel.dev.atti.screens.navigation_bar.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.screens.navigation_bar.home.domain.HomeRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.YearMonth
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    // Caché de meses sincronizados en la sesión para evitar llamadas innecesarias a Supabase
    private val cachedSyncedMonths = mutableSetOf<YearMonth>()

    private val _state = MutableStateFlow(HomeState())

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // 1. Citas del mes reactivas al cambio de mes en el State
    private val monthFollowUpsFlow = _state
        .map { it.currentYearMonth }
        .distinctUntilChanged()
        .flatMapLatest { yearMonth ->
            repository.getFollowUpsForMonthFlow(yearMonth).catch {
                _events.send(HomeEvent.ShowSnackBarError("Error al cargar las citas locales"))
                emit(emptyList())
            }
        }

    // 2. Productos con bajo stock desde Room
    private val lowStockProductsFlow = repository.getLowStockProductsFlow().catch {
        _events.send(HomeEvent.ShowSnackBarError("Error al cargar los productos con bajo stock"))
        emit(emptyList())
    }

    // 3. Estado consolidado (solo 3 flujos: citas del mes, stock bajo y estado local de UI)
    val state: StateFlow<HomeState> = combine(
        monthFollowUpsFlow,
        lowStockProductsFlow,
        _state
    ) { followUps, lowStock, localState ->
        val filteredDayFollowUps = followUps.filter { item ->
            val itemDate = parseIsoToLocalDate(item.followUp.scheduledAt)
            itemDate == localState.selectedDate
        }

        localState.copy(
            monthFollowUps = followUps,
            selectedDayFollowUps = filteredDayFollowUps,
            lowStockProducts = lowStock
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState()
    )

    init {
        val initialMonth = YearMonth.now()
        syncMonthFollowUpsIfNeeded(initialMonth)
        fetchLowStockProducts()
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnSelectDate -> {
                _state.update { it.copy(selectedDate = action.date) }
            }
            HomeAction.OnPreviousMonth -> {
                changeMonth(_state.value.currentYearMonth.minusMonths(1))
            }
            HomeAction.OnNextMonth -> {
                changeMonth(_state.value.currentYearMonth.plusMonths(1))
            }
            HomeAction.OnRetryMonthFollowUps -> {
                syncMonthFollowUpsIfNeeded(_state.value.currentYearMonth, force = true)
            }
            HomeAction.OnRetryLowStock -> {
                fetchLowStockProducts()
            }
            is HomeAction.OnAppointmentClick -> Unit
            is HomeAction.OnProductClick -> Unit
        }
    }

    private fun changeMonth(newMonth: YearMonth) {
        val today = LocalDate.now()
        val newSelectedDate = if (newMonth == YearMonth.from(today)) today else newMonth.atDay(1)

        _state.update {
            it.copy(
                currentYearMonth = newMonth,
                selectedDate = newSelectedDate
            )
        }
        syncMonthFollowUpsIfNeeded(newMonth)
    }

    private fun syncMonthFollowUpsIfNeeded(yearMonth: YearMonth, force: Boolean = false) {
        if (!force && cachedSyncedMonths.contains(yearMonth)) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMonthAppointments = true) }
            repository.syncFollowUpsForMonth(yearMonth)
                .onSuccess {
                    cachedSyncedMonths.add(yearMonth)
                    _state.update { it.copy(isLoadingMonthAppointments = false) }
                }
                .onFailure {
                    _state.update { it.copy(isLoadingMonthAppointments = false) }
                    _events.send(HomeEvent.ShowSnackBarError("Error al sincronizar las citas de ${yearMonth.month}"))
                }
        }
    }

    private fun fetchLowStockProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingLowStock = true) }
            repository.syncLowStockProducts()
                .onSuccess {
                    _state.update { it.copy(isLoadingLowStock = false) }
                }
                .onFailure {
                    _state.update { it.copy(isLoadingLowStock = false) }
                    _events.send(HomeEvent.ShowSnackBarError("Error al sincronizar el stock bajo"))
                }
        }
    }

    private fun parseIsoToLocalDate(isoString: String): LocalDate? {
        if (isoString.isBlank()) return null
        return try {
            val sanitized = isoString.trim().replace(" ", "T")
            if (sanitized.contains("+") || sanitized.endsWith("Z") || Regex("[+-]\\d{2}(:\\d{2})?$").containsMatchIn(sanitized)) {
                OffsetDateTime.parse(sanitized).toLocalDate()
            } else {
                LocalDateTime.parse(sanitized).toLocalDate()
            }
        } catch (e: Exception) {
            null
        }
    }
}