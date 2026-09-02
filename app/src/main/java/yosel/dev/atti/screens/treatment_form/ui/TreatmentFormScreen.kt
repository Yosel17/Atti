package yosel.dev.atti.screens.treatment_form.ui

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
fun TreatmentFormScreen(
    modifier: Modifier = Modifier,
    state: TreatmentFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (TreatmentFormAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Tratamiento" else "Tratamiento",
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
                label = "TreatmentFormScreenAnimation"
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
                            onClickAction = { onAction(TreatmentFormAction.TryLoadAgain) }
                        )
                    }
                    else -> {
                        BodyTreatmentForm(
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
                onDismiss = { onAction(TreatmentFormAction.OnDismissProductSheet) },
                search = state.productSearchQuery,
                onSearchChange = { onAction(TreatmentFormAction.OnProductSearchQueryChange(it)) },
                filteredProducts = state.filteredProducts,
                tempSelectedProductIds = state.tempSelectedProductIds,
                onToggleSelectProduct = { onAction(TreatmentFormAction.OnToggleSelectProduct(it)) },
                onConfirmSelection = { onAction(TreatmentFormAction.OnConfirmProductSelection) },
                productsEmpty = state.productsWithDetails.isEmpty()
            )
        }

        // BottomSheet Servicios
        if (state.isServiceSheetOpen) {
            SelectServiceBottomSheet(
                onDismiss = { onAction(TreatmentFormAction.OnDismissServiceSheet) },
                search = state.serviceSearchQuery,
                onSearchChange = { onAction(TreatmentFormAction.OnServiceSearchQueryChange(it)) },
                filteredServices = state.filteredServices,
                tempSelectedServiceIds = state.tempSelectedServiceIds,
                onToggleSelectService = { onAction(TreatmentFormAction.OnToggleSelectService(it)) },
                onConfirmSelection = { onAction(TreatmentFormAction.OnConfirmServiceSelection) },
                servicesEmpty = state.servicesWithDetails.isEmpty()
            )
        }

        // Diálogos de Carga
        if (state.isLoadingSaveTreatment) {
            LoadingDialog(
                title = "Guardando Tratamiento...",
                subtitle = "Estamos registrando los productos y servicios en el expediente.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }
        if (state.isLoadingUpdateTreatment) {
            LoadingDialog(
                title = "Actualizando Tratamiento...",
                subtitle = "Por favor espera un momento mientras se actualizan los datos...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        // Diálogo de Confirmación
        if (state.showDialogConfirm) {
            SaveTreatmentDialog(
                patientName = state.consultationWithDetails.patientWithDetails.patient.name,
                recordDate = getFormattedCurrentDate(),
                productsCount = state.formInputState.selectedProducts.size,
                servicesCount = state.formInputState.selectedServices.size,
                totalPrice = state.formInputState.totalAmount,
                isEditMode = state.isEditMode,
                onDismiss = { onAction(TreatmentFormAction.ToggleSaveDialog(show = false)) },
                onConfirm = {
                    onAction(TreatmentFormAction.ToggleSaveDialog(show = false))
                    onAction(TreatmentFormAction.SaveTreatment)
                }
            )
        }
    }
}