package yosel.dev.atti.screens.service_form.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import yosel.dev.atti.core.components.AddAppCatalogDialog
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.LoadingDialog
import yosel.dev.atti.core.components.SelectAppCatalogBottomSheet
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.core.utils.Constants

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ServiceFormScreen(
    modifier: Modifier = Modifier,
    state: ServiceFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (ServiceFormAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = if (state.isEditMode) "Editar Servicio" else "Nuevo Servicio",
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
                        targetState.categories.isEmpty() -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "ServiceFormScreenAnimation"
            ) { targetState ->
                when {
                    targetState.isLoadingDataInitial -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(
                                modifier = Modifier.size(75.dp)
                            )
                        }
                    }
                    !targetState.isSuccessGetCategory -> {
                        EmptyGlobal(
                            title = "No se pudo cargar la información inicial",
                            subTitle = "No es posible registrar servicios sin esa información. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(ServiceFormAction.TryCatalogsAgain) }
                        )
                    }
                    else -> {
                        BodyServiceForm(
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

        if (state.isCategorySheetOpen) {
            SelectAppCatalogBottomSheet(
                onDismiss = {
                    onAction(ServiceFormAction.OnDismissCategorySheet)
                },
                title = "Selecciona una categoría",
                search = state.categorySearchQuery,
                onSearchChange = {
                    onAction(ServiceFormAction.OnSearchCategoryQueryChange(it))
                },
                filteredAppCatalogs = state.filteredCategories,
                selectedAppCatalog = state.formInputState.selectedCategory,
                onSelectAppCatalog = { category ->
                    onAction(ServiceFormAction.OnSelectCategory(category = category))
                },
                showAddAppCatalogDialog = {
                    onAction(
                        ServiceFormAction.OnShowAddCatalogDialog(
                            catalogTypeId = Constants.SERVICE_CATEGORY_TYPE_CATALOG,
                            catalogTypeName = "Categoría"
                        )
                    )
                },
                catalogosEmpty = state.categories.isEmpty()
            )
        }

        if (state.isProductSheetOpen) {
            SelectProductBottomSheet(
                onDismiss = {
                    onAction(ServiceFormAction.OnDismissProductSheet)
                },
                search = state.productSearchQuery,
                onSearchChange = {
                    onAction(ServiceFormAction.OnSearchProductQueryChange(it))
                },
                filteredProductsWithDetails = state.filteredProductsWithDetails,
                tempSelectedProductIds = state.tempSelectedProductIds,
                onToggleSelectProduct = { product ->
                    onAction(ServiceFormAction.OnToggleSelectProduct(product))
                },
                onConfirmSelection = {
                    onAction(ServiceFormAction.OnConfirmProductSelection)
                },
                productsWithDetailsEmpty = state.productsWithDetails.isEmpty()
            )
        }

        if (state.isLoadingProducts) {
            LoadingDialog(
                title = "Cargando productos...",
                subtitle = "Estamos obteniendo los insumos disponibles.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }

        if (state.showAddAppCatalogDialog) {
            AddAppCatalogDialog(
                modifier = Modifier.fillMaxWidth(0.9f),
                isLoading = state.isLoadingAddCatalog,
                catalogName = state.activeCatalogTypeName,
                onDismiss = {
                    onAction(ServiceFormAction.OnDismissAddAppCatalogDialog)
                },
                onSave = {
                    onAction(ServiceFormAction.OnSaveAppCatalog(name = it))
                }
            )
        }

        if (state.isLoadingRegisterService) {
            LoadingDialog(
                title = "Registrando servicio...",
                subtitle = "Estamos guardando la información del nuevo servicio.",
                colorTitle = MaterialTheme.colorScheme.primary
            )
        }
    }
}