package yosel.dev.atti.screens.detail_product.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.detail_product.domain.DetailProductRepository

@HiltViewModel(assistedFactory = DetailProductViewModel.Factory::class)
class DetailProductViewModel @AssistedInject constructor(
    private val repository: DetailProductRepository,
    @Assisted private val productId: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(productId: String): DetailProductViewModel
    }

    private val _state = MutableStateFlow(DetailProductState())
    val state: StateFlow<DetailProductState> = _state

    private val _eventChannel = Channel<DetailProductEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        observeProduct()
    }

    private fun observeProduct() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getProductWithDetailsByIdFlow(productId)
                .catch {
                    _state.update { currentState -> currentState.copy(isLoading = false) }
                    _eventChannel.send(
                        DetailProductEvent.ShowErrorSnackbar(
                            message = "No pudimos cargar la información del producto."
                        )
                    )
                }
                .collectLatest { productWithDetailsModel ->
                    _state.update { currentState ->
                        currentState.copy(
                            productWithDetails = productWithDetailsModel ?: currentState.productWithDetails,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onAction(action: DetailProductAction) {
        when (action) {
            DetailProductAction.OnEditClick -> {
                val currentProduct = _state.value.productWithDetails.product
                if (currentProduct.status == Constants.DELETED_STATUS) {
                    _state.update { it.copy(showDialogInformation = true) }
                } else {
                    viewModelScope.launch {
                        _eventChannel.send(
                            DetailProductEvent.OnNavigationMain(
                                Screens.ProductForm(productId = currentProduct.id)
                            )
                        )
                    }
                }
            }

            is DetailProductAction.ToggleShowDialogConfirmDelete -> {
                _state.update { it.copy(showDialogConfirmDelete = action.show) }
            }

            DetailProductAction.DeleteProduct -> deleteProduct()

            is DetailProductAction.ToggleShowDialogConfirmRestore -> {
                _state.update { it.copy(showDialogConfirmRestore = action.show) }
            }

            DetailProductAction.RestoreProduct -> restoreProduct()

            is DetailProductAction.ToggleShowDialogInformation -> {
                _state.update { it.copy(showDialogInformation = action.show) }
            }

            is DetailProductAction.OnNavigationMain -> {
                viewModelScope.launch {
                    _eventChannel.send(DetailProductEvent.OnNavigationMain(action.screen))
                }
            }
        }
    }

    private fun deleteProduct() {
        val currentProduct = _state.value.productWithDetails.product
        _state.update { it.copy(isLoadingDeleteProduct = true) }
        viewModelScope.launch {
            repository.changeStatusProduct(
                productId = currentProduct.id,
                newStatus = Constants.DELETED_STATUS
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoadingDeleteProduct = false,
                            showDialogConfirmDelete = false
                        )
                    }
                    _eventChannel.send(
                        DetailProductEvent.ShowSuccessSnackbar("Producto eliminado exitosamente")
                    )
                },
                onFailure = {
                    _state.update {
                        it.copy(isLoadingDeleteProduct = false, showDialogConfirmDelete = false)
                    }
                    _eventChannel.send(
                        DetailProductEvent.ShowErrorSnackbar("No se pudo eliminar el producto")
                    )
                }
            )
        }
    }

    private fun restoreProduct() {
        val currentProduct = _state.value.productWithDetails.product
        _state.update { it.copy(isLoadingRestoreProduct = true) }
        viewModelScope.launch {
            repository.changeStatusProduct(
                productId = currentProduct.id,
                newStatus = Constants.ACTIVE_STATUS
            ).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoadingRestoreProduct = false,
                            showDialogConfirmRestore = false
                        )
                    }
                    _eventChannel.send(
                        DetailProductEvent.ShowSuccessSnackbar("Producto restaurado exitosamente")
                    )
                },
                onFailure = {
                    _state.update {
                        it.copy(isLoadingRestoreProduct = false, showDialogConfirmRestore = false)
                    }
                    _eventChannel.send(
                        DetailProductEvent.ShowErrorSnackbar("No se pudo restaurar el producto")
                    )
                }
            )
        }
    }
}