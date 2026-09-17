package yosel.dev.atti.screens.main.ui

import android.app.Activity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.launch
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.core.navigation.top_level.TopLevelDestination
import yosel.dev.atti.core.navigation.top_level.ScreensTopLevel
import yosel.dev.atti.core.navigation.top_level.consultationEntry
import yosel.dev.atti.core.navigation.top_level.directoryEntry
import yosel.dev.atti.core.navigation.top_level.homeEntry
import yosel.dev.atti.core.navigation.top_level.inventoryEntry

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigationMain: (Screens) -> Unit
) {
    val navBackStack = rememberNavBackStack(ScreensTopLevel.Home)
    val currentDestination = navBackStack.lastOrNull()
    val activity = LocalContext.current as? Activity

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val navItems = remember {
        listOf(
            TopLevelDestination(
                screen = ScreensTopLevel.Home,
                title = "Inicio",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            ),
            TopLevelDestination(
                screen = ScreensTopLevel.Directory,
                title = "Directorio",
                selectedIcon = Icons.Filled.Folder,
                unselectedIcon = Icons.Outlined.Folder
            ),
            TopLevelDestination(
                screen = ScreensTopLevel.Consultation,
                title = "Consulta",
                selectedIcon = Icons.Filled.MedicalServices,
                unselectedIcon = Icons.Outlined.MedicalServices
            ),
            TopLevelDestination(
                screen = ScreensTopLevel.Inventory,
                title = "Inventario",
                selectedIcon = Icons.Filled.Inventory2,
                unselectedIcon = Icons.Outlined.Inventory2
            )
        )
    }

    ModalNavigationDrawer(
        modifier = modifier.statusBarsPadding(),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Text(
                    text = "Atti",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 28.dp, top = 24.dp, bottom = 16.dp)
                )

                navItems.forEach { item ->
                    val isSelected = currentDestination == item.screen
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            if (navBackStack.contains(item.screen)) {
                                navBackStack.remove(item.screen)
                            }
                            navBackStack.add(item.screen)
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        val currentTitle = navItems.find { it.screen == currentDestination }?.title ?: "Atti"
                        Text(
                            text = currentTitle,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú de navegación"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { innerPadding ->
            NavDisplay(
                modifier = Modifier.padding(innerPadding),
                backStack = navBackStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                onBack = {
                    if (currentDestination != ScreensTopLevel.Home) {
                        if (navBackStack.contains(ScreensTopLevel.Home)) {
                            navBackStack.remove(ScreensTopLevel.Home)
                        }
                        navBackStack.add(ScreensTopLevel.Home)
                    } else {
                        activity?.finish()
                    }
                },
                entryProvider = entryProvider {
                    homeEntry(onNavigationMain = onNavigationMain)
                    directoryEntry(onNavigationMain = onNavigationMain)
                    consultationEntry(onNavigationMain = onNavigationMain)
                    inventoryEntry(onNavigationMain = onNavigationMain)
                }
            )
        }
    }
}