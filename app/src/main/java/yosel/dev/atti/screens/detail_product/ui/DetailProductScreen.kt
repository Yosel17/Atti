package yosel.dev.atti.screens.detail_product.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.DeleteConfirmationDialog
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.core.utils.Constants

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailProductScreen(
    modifier: Modifier = Modifier,
    state: DetailProductState,
    snackBarHostState: SnackbarHostState,
    showEditAction: Boolean,
    onAction: (DetailProductAction) -> Unit,
    onBack: () -> Unit
) {
    val product = state.productWithDetails.product

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = "Detalle Producto",
                onBack = onBack,
                actions = {
                    if (showEditAction){
                        if (!state.isLoading && product.id.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        if (product.status == Constants.DELETED_STATUS) {
                                            onAction(DetailProductAction.ToggleShowDialogInformation(show = true))
                                        } else {
                                            onAction(DetailProductAction.OnEditClick)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Edit,
                                        contentDescription = "Editar producto"
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                if (product.status == Constants.DELETED_STATUS) {
                                    IconButton(
                                        onClick = {
                                            onAction(DetailProductAction.ToggleShowDialogConfirmRestore(show = true))
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Restore,
                                            contentDescription = "Restaurar",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            onAction(DetailProductAction.ToggleShowDialogConfirmDelete(show = true))
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = state,
                contentKey = { targetState ->
                    when {
                        targetState.isLoading -> "LOADING"
                        targetState.productWithDetails.product.id.isEmpty() -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "DetailProductScreenAnimation"
            ) { targetState ->
                when {
                    targetState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(modifier = Modifier.size(75.dp))
                        }
                    }

                    targetState.productWithDetails.product.id.isEmpty() -> {
                        EmptyGlobal(
                            title = "No se pudo encontrar el producto",
                            subTitle = "Intenta de nuevo más tarde",
                            icon = Icons.Outlined.Inventory2
                        )
                    }

                    else -> {
                        BodyDetailProduct(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            state = state
                        )
                    }
                }
            }

            // Diálogo de Confirmación de Eliminación
            if (state.showDialogConfirmDelete) {
                DeleteConfirmationDialog(
                    title = "Eliminar producto",
                    message = "¿Estás seguro de que deseas eliminar el producto?",
                    itemTargetName = product.commercialName,
                    warningNote = "Este registro se ocultará de los productos activos. Podrás volver a activarlo en cualquier momento.",
                    onConfirmDelete = { onAction(DetailProductAction.DeleteProduct) },
                    onDismiss = { onAction(DetailProductAction.ToggleShowDialogConfirmDelete(show = false)) },
                    isLoading = state.isLoadingDeleteProduct
                )
            }

            // Diálogo de Confirmación de Restauración
            if (state.showDialogConfirmRestore) {
                DeleteConfirmationDialog(
                    title = "Restaurar producto",
                    message = "¿Estás seguro de que deseas restaurar el producto?",
                    itemTargetName = product.commercialName,
                    warningNote = "El producto volverá a estar activo y aparecerá nuevamente en las listas principales.",
                    onConfirmDelete = { onAction(DetailProductAction.RestoreProduct) },
                    onDismiss = { onAction(DetailProductAction.ToggleShowDialogConfirmRestore(show = false)) },
                    isLoading = state.isLoadingRestoreProduct,
                    icon = Icons.Outlined.Restore,
                    iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                    confirmButtonText = "Restaurar",
                    buttonContainerColor = MaterialTheme.colorScheme.primary,
                    buttonContentColor = MaterialTheme.colorScheme.onPrimary,
                    textButtonIsLoading = "Restaurando..."
                )
            }

            // Diálogo Informativo si el producto está eliminado e intenta editarse
            if (state.showDialogInformation) {
                DialogInformativeProductEdition(
                    name = product.commercialName,
                    onDismiss = {
                        onAction(DetailProductAction.ToggleShowDialogInformation(show = false))
                    }
                )
            }
        }
    }
}