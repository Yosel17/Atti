package yosel.dev.atti.screens.main.ui

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import yosel.dev.atti.core.navigation.navigation_bar.BottomNavItem
import yosel.dev.atti.core.navigation.navigation_bar.ScreensNavigationBar
import yosel.dev.atti.core.navigation.navigation_bar.consultationEntry
import yosel.dev.atti.core.navigation.navigation_bar.directoryEntry
import yosel.dev.atti.core.navigation.navigation_bar.homeEntry
import yosel.dev.atti.core.navigation.navigation_bar.inventoryEntry

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val bottomNavBackStack = rememberNavBackStack(ScreensNavigationBar.Home)
    val currentDestination = bottomNavBackStack.lastOrNull()

    val activity = LocalContext.current as? Activity

    val navItems = remember {
        listOf(
            BottomNavItem(
                screen = ScreensNavigationBar.Home,
                title = "Inicio",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            ),
            BottomNavItem(
                screen = ScreensNavigationBar.Directory,
                title = "Directorio",
                selectedIcon = Icons.Filled.Folder,
                unselectedIcon = Icons.Outlined.Folder
            ),
            BottomNavItem(
                screen = ScreensNavigationBar.Consultation,
                title = "Consulta",
                selectedIcon = Icons.Filled.MedicalServices,
                unselectedIcon = Icons.Outlined.MedicalServices
            ),
            BottomNavItem(
                screen = ScreensNavigationBar.Inventory,
                title = "Inventario",
                selectedIcon = Icons.Filled.Inventory2,
                unselectedIcon = Icons.Outlined.Inventory2
            )
        )
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar{
                navItems.forEach { item ->
                    val isSelected = currentDestination == item.screen

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            // En lugar de .clear(), reordenamos el backstack para conservar la instancia de la pestaña
                            if (bottomNavBackStack.contains(item.screen)) {
                                bottomNavBackStack.remove(item.screen)
                            }
                            bottomNavBackStack.add(item.screen)
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(text = item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = bottomNavBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            onBack = {
                // Evaluamos el comportamiento del botón de retroceso
                if (currentDestination != ScreensNavigationBar.Home) {
                    // Si no estamos en Inicio, llevamos la pantalla de Inicio al frente.
                    // Al no eliminar la pantalla actual usando removeLastOrNull(),
                    // su estado y ViewModel se mantienen vivos en memoria.
                    if (bottomNavBackStack.contains(ScreensNavigationBar.Home)) {
                        bottomNavBackStack.remove(ScreensNavigationBar.Home)
                    }
                    bottomNavBackStack.add(ScreensNavigationBar.Home)
                } else {
                    // Si ya estamos en la pantalla de Inicio y se presiona Atrás,
                    // finalizamos la Activity para salir de la aplicación.
                    activity?.finish()
                }
            },
            entryProvider = entryProvider {
                homeEntry()
                directoryEntry()
                consultationEntry()
                inventoryEntry()
            }
        )
    }
}