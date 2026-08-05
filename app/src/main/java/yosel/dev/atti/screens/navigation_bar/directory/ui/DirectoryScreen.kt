package yosel.dev.atti.screens.navigation_bar.directory.ui

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
import androidx.compose.material.icons.filled.Pets
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
            visible = state.selectedTabIndex == 0 && state.clients.isNotEmpty(),
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

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            visible = state.selectedTabIndex == 1 && state.patients.isNotEmpty(),
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
                onClick = {

                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Pets,
                        contentDescription = "new patient"
                    )
                },
                text = { Text(text = "Agregar paciente") },
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