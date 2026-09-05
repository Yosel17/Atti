package yosel.dev.atti.screens.receipt_form.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.LoadingDialog
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.core.utils.getFormattedCurrentDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReceiptFormScreen(
    modifier: Modifier = Modifier,
    state: ReceiptFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (ReceiptFormAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Recibo" else "Generar Recibo",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
        ) {
            AnimatedContent(
                targetState = state,
                contentKey = { targetState ->
                    when {
                        targetState.isLoadingDataInitial -> "LOADING"
                        !targetState.isSuccessGetData -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "ReceiptFormScreenAnimation"
            ) { targetState ->
                when {
                    targetState.isLoadingDataInitial -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(modifier = Modifier.size(75.dp))
                        }
                    }
                    !targetState.isSuccessGetData -> {
                        EmptyGlobal(
                            title = "No se pudo cargar la información inicial",
                            subTitle = "No fue posible obtener los productos o servicios necesarios. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(ReceiptFormAction.TryLoadAgain) }
                        )
                    }
                    else -> {
                        BodyReceiptForm(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            state = state,
                            onAction = onAction
                        )
                    }
                }
            }
        }

        // BottomSheet Productos
        if (state.isProductSheetOpen) {
            SelectProductBottomSheet(
                onDismiss = { onAction(ReceiptFormAction.OnDismissProductSheet) },
                search = state.productSearchQuery,
                onSearchChange = { onAction(ReceiptFormAction.OnProductSearchQueryChange(it)) },
                filteredProducts = state.filteredProducts,
                tempSelectedProductIds = state.tempSelectedProductIds,
                onToggleSelectProduct = { onAction(ReceiptFormAction.OnToggleSelectProduct(it)) },
                onConfirmSelection = { onAction(ReceiptFormAction.OnConfirmProductSelection) },
                productsEmpty = state.productsWithDetails.isEmpty()
            )
        }

        // BottomSheet Servicios
        if (state.isServiceSheetOpen) {
            SelectServiceBottomSheet(
                onDismiss = { onAction(ReceiptFormAction.OnDismissServiceSheet) },
                search = state.serviceSearchQuery,
                onSearchChange = { onAction(ReceiptFormAction.OnServiceSearchQueryChange(it)) },
                filteredServices = state.filteredServices,
                tempSelectedServiceIds = state.tempSelectedServiceIds,
                onToggleSelectService = { onAction(ReceiptFormAction.OnToggleSelectService(it)) },
                onConfirmSelection = { onAction(ReceiptFormAction.OnConfirmServiceSelection) },
                servicesEmpty = state.servicesWithDetails.isEmpty()
            )
        }

        // Diálogos de Carga
        if (state.isLoadingSaveReceipt) {
            LoadingDialog(
                title = "Guardando Recibo...",
                subtitle = "Estamos registrando los cobros y detalles en el sistema.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }
        if (state.isLoadingUpdateReceipt) {
            LoadingDialog(
                title = "Actualizando Recibo...",
                subtitle = "Por favor espera un momento mientras se actualizan los datos...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        // Diálogo de Confirmación
        if (state.showDialogConfirm) {
            SaveReceiptDialog(
                customerName = state.formInputState.customerName,
                recordDate = getFormattedCurrentDate(),
                productsCount = state.formInputState.selectedProducts.size,
                servicesCount = state.formInputState.selectedServices.size,
                subtotalPrice = state.formInputState.subtotalAmount,
                totalPrice = state.formInputState.totalAmount,
                isEditMode = state.isEditMode,
                onDismiss = { onAction(ReceiptFormAction.ToggleSaveDialog(show = false)) },
                onConfirm = {
                    onAction(ReceiptFormAction.ToggleSaveDialog(show = false))
                    onAction(ReceiptFormAction.SaveReceipt)
                }
            )
        }
    }
}