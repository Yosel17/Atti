package yosel.dev.atti.screens.top_level.receipts.ui

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
import yosel.dev.atti.screens.top_level.receipts.domain.ReceiptsRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class ReceiptsViewModel @Inject constructor(
    private val repository: ReceiptsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReceiptsState())
    private val _events = Channel<ReceiptsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val debouncedQuery = _state
        .map { it.searchQuery }
        .distinctUntilChanged()
        .debounce(300.milliseconds)

    private val receiptsFlow = combine(
        repository.getAllReceiptsWithDetails().catch {
            _events.send(ReceiptsEvent.ShowSnackBarError("Error al cargar los recibos locales"))
            emit(emptyList())
        },
        debouncedQuery
    ) { receipts, query ->
        val queryNorm = query.normalize()
        val filtered = receipts.filter { item ->
            if (queryNorm.isBlank()) return@filter true
            val r = item.receipt
            val matchesNumber = r.receiptNumber.toString().contains(queryNorm)
            val matchesCustomer = r.customerName.normalize().contains(queryNorm)
            val matchesNotes = r.notes.normalize().contains(queryNorm)
            val matchesPatient = item.consultationWithDetails?.patientWithDetails?.patient?.name?.normalize()?.contains(queryNorm) == true
            val matchesClient = item.consultationWithDetails?.patientWithDetails?.client?.let { client ->
                "${client.firstName} ${client.lastName}".normalize().contains(queryNorm)
            } == true
            val matchesItems = item.items.any { itemDetails ->
                itemDetails.product?.product?.commercialName?.normalize()?.contains(queryNorm) == true ||
                        itemDetails.service?.service?.name?.normalize()?.contains(queryNorm) == true
            }

            matchesNumber || matchesCustomer || matchesNotes || matchesPatient || matchesClient || matchesItems
        }
        receipts to filtered
    }

    val state: StateFlow<ReceiptsState> = combine(receiptsFlow, _state) { (receipts, filtered), localState ->
        localState.copy(
            receipts = receipts,
            filteredReceipts = filtered
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReceiptsState()
    )

    init {
        syncRemoteReceipts()
    }

    fun onAction(action: ReceiptsAction) {
        when (action) {
            is ReceiptsAction.OnSearchQueryChange -> _state.update { it.copy(searchQuery = action.query) }
        }
    }

    private fun syncRemoteReceipts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.syncReceipts()
                .onSuccess { _state.update { it.copy(isLoading = false) } }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ReceiptsEvent.ShowSnackBarError("Error al sincronizar recibos"))
                }
        }
    }
}