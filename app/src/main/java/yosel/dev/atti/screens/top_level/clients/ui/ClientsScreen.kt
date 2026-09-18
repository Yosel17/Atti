package yosel.dev.atti.screens.top_level.clients.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yosel.dev.atti.core.components.ClientFilterBottomSheet
import yosel.dev.atti.core.components.SnackBarError
import yosel.dev.atti.core.navigation.main.Screens

@Composable
fun ClientsScreen(
    modifier: Modifier = Modifier,
    state: ClientsState,
    snackBarHostState: SnackbarHostState,
    onAction: (ClientsAction) -> Unit,
    onNavigationMain: (Screens) -> Unit
) {


    Box(modifier = modifier.fillMaxSize()) {
        BodyClients(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onAction = onAction,
            onNavigationMain = onNavigationMain
        )

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomEnd),
            visible = state.clients.isNotEmpty() && !state.isLoading,
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            ) + scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            ) + fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)),
            exit = slideOutVertically(
                targetOffsetY = { it / 2 },
                animationSpec = spring(stiffness = Spring.StiffnessLow)
            ) + scaleOut(
                targetScale = 0.8f,
                animationSpec = spring(stiffness = Spring.StiffnessLow)
            ) + fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow))
        ) {
            ExtendedFloatingActionButton(
                onClick = { onNavigationMain(Screens.AddClient) },
                icon = { Icon(imageVector = Icons.Filled.PersonAdd, contentDescription = "Nuevo cliente") },
                text = { Text(text = "Agregar cliente") },
                expanded = true,
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
            )
        }

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) { data ->
            SnackBarError(data = data)
        }
    }

    if (state.showFilterSheet) {
        ClientFilterBottomSheet(
            initialFilter = state.filter,
            onDismissRequest = { onAction(ClientsAction.OnToggleFilterSheet(false)) },
            onApply = { onAction(ClientsAction.OnApplyFilter(it)) }
        )
    }
}