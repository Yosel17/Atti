package yosel.dev.atti.screens.main.ui

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
                            if (!isSelected) {
                                bottomNavBackStack.clear()
                                bottomNavBackStack.add(item.screen)
                            }
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
                bottomNavBackStack.removeLastOrNull()
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