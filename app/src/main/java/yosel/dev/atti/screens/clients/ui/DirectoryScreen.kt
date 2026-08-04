package yosel.dev.atti.screens.clients.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
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
    onNavigation:(Screens) -> Unit,
    onAction: (DirectoryAction) -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState){ data ->
                SnackBarError(data = data)
            }
        },
        floatingActionButton = {
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
                expanded = true
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BodyDirectory(
                modifier = Modifier
                    .fillMaxSize(),
                state = state,
                onClientClick = { idClient ->

                },
                onAction = onAction
            )
        }
    }
}