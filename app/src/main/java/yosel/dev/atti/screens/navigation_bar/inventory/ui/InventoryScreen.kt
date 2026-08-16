package yosel.dev.atti.screens.navigation_bar.inventory.ui

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
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
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

@Composable
fun InventoryScreen(
    modifier: Modifier = Modifier,
    state: InventoryState,
    snackBarHostState: SnackbarHostState,
    onAction: (InventoryAction) -> Unit,
    onNavigationMain: (Screens) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        BodyInventory(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onAction = onAction,
            onNavigationMain = onNavigationMain
        )

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            visible = state.selectedTabIndex == 0 && state.products.isNotEmpty() && !state.isLoadingProducts,
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
        ){
            ExtendedFloatingActionButton(
                onClick = {
                    onNavigationMain(Screens.ProductForm())
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Medication,
                        contentDescription = "new product"
                    )
                },
                text = { Text(text = "Agregar Producto") },
                expanded = true,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 16.dp)
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            visible = state.selectedTabIndex == 1 && state.services.isNotEmpty() && !state.isLoadingServices,
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
        ){
            ExtendedFloatingActionButton(
                onClick = {
                    onNavigationMain(Screens.ServiceForm())
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.MedicalServices,
                        contentDescription = "new services"
                    )
                },
                text = { Text(text = "Agregar Servicio") },
                expanded = true,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 16.dp)
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            visible = state.selectedTabIndex == 2 && state.suppliers.isNotEmpty() && !state.isLoadingSuppliers,
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
        ){
            ExtendedFloatingActionButton(
                onClick = {
                    onNavigationMain(Screens.AddSupplier)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.LocalShipping,
                        contentDescription = "new suppliers"
                    )
                },
                text = { Text(text = "Agregar Proveedor") },
                expanded = true,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 16.dp)
            )
        }

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