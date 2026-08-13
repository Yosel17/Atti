package yosel.dev.atti.screens.detail_supplier.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
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
import yosel.dev.atti.core.components.LoadingDialog
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.core.utils.Constants

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailSupplierScreen(
    modifier: Modifier = Modifier,
    state: DetailSupplierState,
    snackBarHostState: SnackbarHostState,
    onAction: (DetailSupplierAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = "Detalle Proveedor",
                onBack = onBack,
                actions = {
                    if (!state.isLoading && state.supplier.id.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (state.supplier.status == Constants.DELETED_STATUS) {
                                        onAction(DetailSupplierAction.ToggleShowDialogInformation(show = true))
                                    } else {
                                        onAction(DetailSupplierAction.OnEditClick)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = "Editar proveedor"
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            if (state.supplier.status == Constants.DELETED_STATUS) {
                                IconButton(
                                    onClick = {
                                        onAction(DetailSupplierAction.ToggleShowDialogConfirmRestore(show = true))
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
                                        onAction(DetailSupplierAction.ToggleShowDialogConfirmDelete(show = true))
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
                        targetState.supplier.id.isEmpty() -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "DetailSupplierScreenAnimation"
            ) { targetState ->
                when {
                    targetState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(modifier = Modifier.size(75.dp))
                        }
                    }
                    targetState.supplier.id.isEmpty() -> {
                        EmptyGlobal(
                            title = "No se pudo encontrar al proveedor",
                            subTitle = "Intenta de nuevo más tarde",
                            icon = Icons.Outlined.LocalShipping
                        )
                    }
                    else -> {
                        BodyDetailSupplier(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            state = state,
                            onAction = onAction
                        )
                    }
                }
            }

            // BottomSheet de Edición
            if (state.isEditing) {
                EditSupplierBottomSheet(
                    state = state,
                    onAction = onAction
                )
            }

            // Loading Dialog durante actualización
            if (state.isLoadingUpdate) {
                LoadingDialog(
                    title = "Actualizando información...",
                    subtitle = "Por favor espera un momento..."
                )
            }

            // Diálogo de Confirmación de Eliminación
            if (state.showDialogConfirmDelete) {
                DeleteConfirmationDialog(
                    title = "Eliminar proveedor",
                    message = "¿Estás seguro de que deseas eliminar al proveedor?",
                    itemTargetName = state.supplier.name,
                    warningNote = "Este registro se ocultará de los proveedores activos. Podrás volver a activarlo en cualquier momento.",
                    onConfirmDelete = { onAction(DetailSupplierAction.DeleteSupplier) },
                    onDismiss = { onAction(DetailSupplierAction.ToggleShowDialogConfirmDelete(show = false)) },
                    isLoading = state.isLoadingDeleteSupplier
                )
            }

            // Diálogo de Confirmación de Restauración
            if (state.showDialogConfirmRestore) {
                DeleteConfirmationDialog(
                    title = "Restaurar proveedor",
                    message = "¿Estás seguro de que deseas restaurar al proveedor?",
                    itemTargetName = state.supplier.name,
                    warningNote = "El proveedor volverá a estar activo y aparecerá nuevamente en las listas principales.",
                    onConfirmDelete = { onAction(DetailSupplierAction.RestoreSupplier) },
                    onDismiss = { onAction(DetailSupplierAction.ToggleShowDialogConfirmRestore(show = false)) },
                    isLoading = state.isLoadingRestoreSupplier,
                    icon = Icons.Outlined.Restore,
                    iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                    confirmButtonText = "Restaurar",
                    buttonContainerColor = MaterialTheme.colorScheme.primary,
                    buttonContentColor = MaterialTheme.colorScheme.onPrimary,
                    textButtonIsLoading = "Restaurando..."
                )
            }

            // Diálogo Informativo si está eliminado e intenta editar
            if (state.showDialogInformation) {
                DialogInformativeSupplierEdition(
                    name = state.supplier.name,
                    onDismiss = {
                        onAction(DetailSupplierAction.ToggleShowDialogInformation(show = false))
                    }
                )
            }
        }
    }
}