package yosel.dev.atti.screens.pre_anesthetic_test_form.ui

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
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.utils.getFormattedCurrentDate

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PreAnestheticTestFormScreen(
    modifier: Modifier = Modifier,
    state: PreAnestheticTestFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (PreAnestheticTestFormAction) -> Unit,
    onBack: () -> Unit,
    onNavigation: (Screens) -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Exámenes Pre Anestésicos" else "Exámenes Pre Anestésicos",
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
                label = "PreAnestheticTestFormScreenAnimation"
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
                            onClickAction = { onAction(PreAnestheticTestFormAction.TryLoadAgain) }
                        )
                    }
                    else -> {
                        BodyPreAnestheticTestForm(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            state = state,
                            onAction = onAction,
                            onNavigation = onNavigation
                        )
                    }
                }
            }
        }

        // BottomSheet Productos
        if (state.isProductSheetOpen) {
            SelectProductBottomSheet(
                onDismiss = { onAction(PreAnestheticTestFormAction.OnDismissProductSheet) },
                search = state.productSearchQuery,
                onSearchChange = { onAction(PreAnestheticTestFormAction.OnProductSearchQueryChange(it)) },
                filteredProducts = state.filteredProducts,
                tempSelectedProductIds = state.tempSelectedProductIds,
                onToggleSelectProduct = { onAction(PreAnestheticTestFormAction.OnToggleSelectProduct(it)) },
                onConfirmSelection = { onAction(PreAnestheticTestFormAction.OnConfirmProductSelection) },
                productsEmpty = state.productsWithDetails.isEmpty()
            )
        }

        // BottomSheet Servicios
        if (state.isServiceSheetOpen) {
            SelectServiceBottomSheet(
                onDismiss = { onAction(PreAnestheticTestFormAction.OnDismissServiceSheet) },
                search = state.serviceSearchQuery,
                onSearchChange = { onAction(PreAnestheticTestFormAction.OnServiceSearchQueryChange(it)) },
                filteredServices = state.filteredServices,
                tempSelectedServiceIds = state.tempSelectedServiceIds,
                onToggleSelectService = { onAction(PreAnestheticTestFormAction.OnToggleSelectService(it)) },
                onConfirmSelection = { onAction(PreAnestheticTestFormAction.OnConfirmServiceSelection) },
                servicesEmpty = state.servicesWithDetails.isEmpty()
            )
        }

        // Diálogos de Carga
        if (state.isLoadingSavePreAnestheticTest) {
            LoadingDialog(
                title = "Guardando Exámenes Pre Anestésicos...",
                subtitle = "Estamos registrando los productos y servicios en el expediente.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }
        if (state.isLoadingUpdatePreAnestheticTest) {
            LoadingDialog(
                title = "Actualizando Exámenes Pre Anestésicos...",
                subtitle = "Por favor espera un momento mientras se actualizan los datos...",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        // Diálogo de Confirmación
        if (state.showDialogConfirm) {
            SavePreAnestheticTestDialog(
                patientName = state.consultationWithDetails.patientWithDetails.patient.name,
                recordDate = getFormattedCurrentDate(),
                productsCount = state.formInputState.selectedProducts.size,
                servicesCount = state.formInputState.selectedServices.size,
                totalPrice = state.formInputState.totalAmount,
                isEditMode = state.isEditMode,
                onDismiss = { onAction(PreAnestheticTestFormAction.ToggleSaveDialog(show = false)) },
                onConfirm = {
                    onAction(PreAnestheticTestFormAction.ToggleSaveDialog(show = false))
                    onAction(PreAnestheticTestFormAction.SavePreAnestheticTest)
                }
            )
        }
    }
}
