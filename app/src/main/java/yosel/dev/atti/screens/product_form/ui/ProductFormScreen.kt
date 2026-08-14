package yosel.dev.atti.screens.product_form.ui

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.SelectAppCatalogBottomSheet
import yosel.dev.atti.core.components.TopBarGlobal

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductFormScreen(
    modifier: Modifier = Modifier,
    state: ProductFormState,
    snackBarHostState: SnackbarHostState,
    onAction: (ProductFormAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = "Registrar Producto",
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
        ){
            AnimatedContent(
                targetState = state,
                contentKey = { targetState ->
                    when{
                        targetState.isLoadingDataInitial -> "LOADING"
                        targetState.categories.isEmpty() && targetState.unitsOfMeasurement.isEmpty() -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "ProductFormScreenAnimation"
            ){ targetState ->
                when {
                    targetState.isLoadingDataInitial ->{
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(
                                modifier = Modifier.size(75.dp)
                            )
                        }
                    }
                    !targetState.isSuccessGetCategory && !targetState.isSuccessGetSuppliers -> {
                        EmptyGlobal(
                            title = "No se pudo cargar la información inicial",
                            subTitle = "No es posible registrar products sin esa información. Inténtalo de nuevo.",
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            showAction = true,
                            onClickAction = { onAction(ProductFormAction.TryCatalogsAgain) }
                        )
                    }
                    else -> {
                        BodyProductForm(
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

        if (state.isCategorySheetOpen){
            SelectAppCatalogBottomSheet(
                onDismiss = {
                    onAction(ProductFormAction.OnDismissCategorySheet)
                },
                title = "Selecciona una categoría",
                search = state.categorySearchQuery,
                onSearchChange = {
                    onAction(ProductFormAction.OnSearchCategoryQueryChange(it))
                },
                filteredAppCatalogs = state.filteredCategories,
                selectedAppCatalog = state.formInputState.selectedCategory,
                onSelectAppCatalog = { category ->
                    onAction(ProductFormAction.OnSelectCategory(category = category))
                }
            )
        }
    }
}