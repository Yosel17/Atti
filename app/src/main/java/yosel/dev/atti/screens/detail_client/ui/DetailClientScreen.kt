package yosel.dev.atti.screens.detail_client.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.CustomSnackbarHost
import yosel.dev.atti.core.components.DeleteConfirmationDialog
import yosel.dev.atti.core.components.EmptyGlobal
import yosel.dev.atti.core.components.LoadingDialog
import yosel.dev.atti.core.components.TopBarGlobal
import yosel.dev.atti.core.utils.Constants
import yosel.dev.atti.screens.detail_patient.ui.DetailPatientAction
import yosel.dev.atti.ui.theme.AttiTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailClientScreen(
    modifier: Modifier = Modifier,
    state: DetailClientState,
    snackBarHostState: SnackbarHostState,
    onAction: (DetailClientAction) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            CustomSnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TopBarGlobal(
                title = "",
                onBack = onBack,
                actions = {
                    if (!state.isLoading && state.clientWithPatients.client.id.isNotEmpty()){
                        IconButton(
                            onClick = {
                                if (state.clientWithPatients.client.status == Constants.DELETED_CLIENT_STATUS){
                                    onAction(
                                        DetailClientAction.ToggleShowDialogInformation(show = true)
                                    )
                                }else{
                                    onAction(DetailClientAction.OnEditClick)
                                }
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "editar"
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        if (state.clientWithPatients.client.status == Constants.DELETED_CLIENT_STATUS){
                            IconButton(
                                onClick = {
                                    onAction(
                                        DetailClientAction.ToggleShowDialogConfirmRestore(show = true)
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Restore,
                                    contentDescription = "Restaurar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }else{
                            IconButton(
                                onClick = {
                                    onAction(
                                        DetailClientAction.ToggleShowDialogConfirmDelete(show = true)
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = "eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
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
        ){
            AnimatedContent(
                targetState = state,
                contentKey = { targetState ->
                    when{
                        targetState.isLoading -> "LOADING"
                        targetState.clientWithPatients.client.id.isEmpty() -> "EMPTY"
                        else -> "CONTENT"
                    }
                },
                label = "DetailClientScreenAnimation"
            ){ targetState ->
                when {
                    targetState.isLoading ->{
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            LoadingIndicator(
                                modifier = Modifier.size(75.dp)
                            )
                        }
                    }
                    targetState.clientWithPatients.client.id.isEmpty() ->{
                        EmptyGlobal(
                            title = "No se pudo encontrar al cliente",
                            subTitle = "Intenta de nuevo más tarde"
                        )
                    }
                    else ->{
                        BodyDetailClient(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            state = state,
                            onAction = onAction
                        )
                    }
                }
            }

            if (state.isEditing) {
                EditClientBottomSheet(
                    state = state,
                    onAction = onAction
                )
            }

            if (state.isLoadingUpdate) {
                LoadingDialog(
                    title = "Actualizando información",
                    subtitle = "Por favor espera un momento..."
                )
            }

            if (state.showDialogConfirmDelete){
                DeleteConfirmationDialog(
                    title = "Eliminar cliente",
                    message = "¿Estás seguro de que deseas eliminar al cliente",
                    itemTargetName = "${state.clientWithPatients.client.firstName} ${state.clientWithPatients.client.lastName}",
                    warningNote = "Este registro se ocultará de los clientes activos junto con toda su información vinculada. Podrás volver a activarlo en cualquier momento.",
                    onConfirmDelete = { onAction(DetailClientAction.DeleteClient) },
                    onDismiss = { onAction(DetailClientAction.ToggleShowDialogConfirmDelete(show = false)) },
                    isLoading = state.isLoadingDeleteClient
                )
            }

            if (state.showDialogConfirmRestore){
                DeleteConfirmationDialog(
                    title = "Restaurar cliente",
                    message = "¿Estás seguro de que deseas restaurar al cliente",
                    itemTargetName = "${state.clientWithPatients.client.firstName} ${state.clientWithPatients.client.lastName}",
                    warningNote = "El cliente volverá a estar activo y su información vinculada aparecerá nuevamente en las listas principales.",
                    onConfirmDelete = { onAction(DetailClientAction.RestoreClient) },
                    onDismiss = { onAction(DetailClientAction.ToggleShowDialogConfirmRestore(show = false)) },
                    isLoading = state.isLoadingRestoreClient,
                    icon = Icons.Outlined.Restore,
                    iconBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                    confirmButtonText = "Restaurar",
                    buttonContainerColor = MaterialTheme.colorScheme.primary,
                    buttonContentColor = MaterialTheme.colorScheme.onPrimary,
                    textButtonIsLoading = "Restaurando..."
                )
            }

            if (state.showDialogInformation){
                DialogInformativeEdition(
                    name = "${state.clientWithPatients.client.firstName} ${state.clientWithPatients.client.lastName}",
                    onDismiss = {
                        onAction(DetailClientAction.ToggleShowDialogInformation(show = false))
                    }
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun DetailClientScreenPreview() {
    AttiTheme {
        DetailClientScreen(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            state = DetailClientState(
                isLoading = false
            ),
            snackBarHostState = SnackbarHostState(),
            onAction = {},
            onBack = {}
        )
    }
}