package yosel.dev.atti.screens.detail_service.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MedicalServices
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
fun DetailServiceScreen(
    modifier: Modifier = Modifier,
    state: DetailServiceState,
    snackBarHostState: SnackbarHostState,
    onAction: (DetailServiceAction) -> Unit,
    onBack: () -> Unit
) {
    val service = state.serviceWithDetails.service

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = "Detalle Servicio",
                onBack = onBack,
                actions = {
                    if (!state.isLoading && service.id.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (service.status == Constants.DELETED_STATUS) {
                                        onAction(DetailServiceAction.ToggleShowDialogInformation(show = true))
                                    } else {
                                        onAction(DetailServiceAction.OnEditClick)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = "Editar servicio"
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            if (service.status == Constants.DELETED_STATUS) {
                                IconButton(
                                    onClick = {
                                        onAction(DetailServiceAction.ToggleShowDialogConfirmRestore(show = true))
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
                                        onAction(DetailServiceAction.ToggleShowDialogConfirmDelete(show = true))
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
                        targetState.serviceWithDetails.service.id.isEmpty() -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "DetailServiceScreenAnimation"
            ) { targetState ->
                when {
                    targetState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(modifier = Modifier.size(75.dp))
                        }
                    }
                    targetState.serviceWithDetails.service.id.isEmpty() -> {
                        EmptyGlobal(
                            title = "No se pudo encontrar el servicio",
                            subTitle = "Intenta de nuevo más tarde",
                            icon = Icons.Outlined.MedicalServices
                        )
                    }
                    else -> {
                        BodyDetailService(
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
                    title = "Eliminar servicio",
                    message = "¿Estás seguro de que deseas eliminar el servicio?",
                    itemTargetName = service.name,
                    warningNote = "Este registro se ocultará de los servicios activos. Podrás volver a activarlo en cualquier momento.",
                    onConfirmDelete = { onAction(DetailServiceAction.DeleteService) },
                    onDismiss = { onAction(DetailServiceAction.ToggleShowDialogConfirmDelete(show = false)) },
                    isLoading = state.isLoadingDeleteService
                )
            }

            // Diálogo de Confirmación de Restauración
            if (state.showDialogConfirmRestore) {
                DeleteConfirmationDialog(
                    title = "Restaurar servicio",
                    message = "¿Estás seguro de que deseas restaurar el servicio?",
                    itemTargetName = service.name,
                    warningNote = "El servicio volverá a estar activo y aparecerá nuevamente en las listas principales.",
                    onConfirmDelete = { onAction(DetailServiceAction.RestoreService) },
                    onDismiss = { onAction(DetailServiceAction.ToggleShowDialogConfirmRestore(show = false)) },
                    isLoading = state.isLoadingRestoreService,
                    icon = Icons.Outlined.Restore,
                    iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                    confirmButtonText = "Restaurar",
                    buttonContainerColor = MaterialTheme.colorScheme.primary,
                    buttonContentColor = MaterialTheme.colorScheme.onPrimary,
                    textButtonIsLoading = "Restaurando..."
                )
            }

            // Diálogo Informativo si el servicio está eliminado e intenta editarse
            if (state.showDialogInformation) {
                DialogInformativeServiceEdition(
                    name = service.name,
                    onDismiss = {
                        onAction(DetailServiceAction.ToggleShowDialogInformation(show = false))
                    }
                )
            }
        }
    }
}