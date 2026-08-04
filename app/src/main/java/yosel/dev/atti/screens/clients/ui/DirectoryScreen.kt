package yosel.dev.atti.screens.clients.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.SnackBarError
import yosel.dev.atti.core.navigation.main.Screens

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DirectoryScreen(
    modifier: Modifier = Modifier,
    state: DirectoryState,
    snackBarHostState: SnackbarHostState,
    onNavigation: (Screens) -> Unit,
    onAction: (DirectoryAction) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 1. Contenido principal
        BodyDirectory(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onClientClick = { idClient ->

            },
            onAction = onAction
        )

        // 2. Floating Action Button (Abajo a la derecha)
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            visible = state.selectedTabIndex == 0 && !state.isLoadingClients && state.clients.isNotEmpty()
        ) {
            ExtendedFloatingActionButton(
                onClick = {

                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.PersonAdd,
                        contentDescription = "new client"
                    )
                },
                text = { Text(text = "Agregar cliente") },
                expanded = true,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 16.dp)
            )
        }

        // 3. Snackbar Host (Abajo al centro)
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) { data ->
            SnackBarError(data = data)
        }
    }
}