package yosel.dev.atti.screens.main.ui

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
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
import yosel.dev.atti.core.navigation.top_level.ScreensTopLevel
import yosel.dev.atti.core.navigation.top_level.TopLevelDestination
import yosel.dev.atti.core.navigation.top_level.clientsEntry
import yosel.dev.atti.core.navigation.top_level.consultationEntry
import yosel.dev.atti.core.navigation.top_level.homeEntry
import yosel.dev.atti.core.navigation.top_level.patientsEntry
import yosel.dev.atti.core.navigation.top_level.productsEntry
import yosel.dev.atti.core.navigation.top_level.receiptsEntry
import yosel.dev.atti.core.navigation.top_level.servicesEntry
import yosel.dev.atti.core.navigation.top_level.suppliersEntry

private data class NavDrawerSection(
    val title: String,
    val items: List<TopLevelDestination>
)

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

    val drawerSections = remember {
        listOf(
            NavDrawerSection(
                title = "Clínica",
                items = listOf(
                    TopLevelDestination(
                        screen = ScreensTopLevel.Home,
                        title = "Inicio",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home
                    ),
                    TopLevelDestination(
                        screen = ScreensTopLevel.Consultation,
                        title = "Consulta",
                        selectedIcon = Icons.Filled.MedicalServices,
                        unselectedIcon = Icons.Outlined.MedicalServices
                    ),
                    TopLevelDestination(
                        screen = ScreensTopLevel.Receipts,
                        title = "Recibos",
                        selectedIcon = Icons.Filled.LocalShipping,
                        unselectedIcon = Icons.Outlined.LocalShipping
                    )
                )
            ),
            NavDrawerSection(
                title = "Inventario",
                items = listOf(
                    TopLevelDestination(
                        screen = ScreensTopLevel.Products,
                        title = "Productos",
                        selectedIcon = Icons.Filled.Medication,
                        unselectedIcon = Icons.Outlined.Medication
                    ),
                    TopLevelDestination(
                        screen = ScreensTopLevel.Services,
                        title = "Servicios",
                        selectedIcon = Icons.Filled.MedicalServices,
                        unselectedIcon = Icons.Outlined.MedicalServices
                    ),
                    TopLevelDestination(
                        screen = ScreensTopLevel.Suppliers,
                        title = "Proveedores",
                        selectedIcon = Icons.Filled.LocalShipping,
                        unselectedIcon = Icons.Outlined.LocalShipping
                    )
                )
            ),
            NavDrawerSection(
                title = "Directorio",
                items = listOf(
                    TopLevelDestination(
                        screen = ScreensTopLevel.Clients,
                        title = "Clientes",
                        selectedIcon = Icons.Filled.People,
                        unselectedIcon = Icons.Outlined.People
                    ),
                    TopLevelDestination(
                        screen = ScreensTopLevel.Patients,
                        title = "Pacientes",
                        selectedIcon = Icons.Filled.Pets,
                        unselectedIcon = Icons.Outlined.Pets
                    )
                )
            )
        )
    }

    val currentTitle = remember(currentDestination, drawerSections) {
        drawerSections.flatMap { it.items }.find { it.screen == currentDestination }?.title ?: "Atti"
    }

    ModalNavigationDrawer(
        modifier = modifier.statusBarsPadding(),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Atti",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 28.dp, top = 24.dp, bottom = 12.dp)
                    )
                    drawerSections.forEachIndexed { index, section ->
                        Text(
                            text = section.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
                        )
                        section.items.forEach { item ->
                            val isSelected = currentDestination == item.screen
                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
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
                        if (index < drawerSections.lastIndex) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 28.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = currentTitle,
                            style = MaterialTheme.typography.titleLarge
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
                    consultationEntry(onNavigationMain = onNavigationMain)
                    productsEntry(onNavigationMain = onNavigationMain)
                    servicesEntry(onNavigationMain = onNavigationMain)
                    suppliersEntry(onNavigationMain = onNavigationMain)
                    clientsEntry(onNavigationMain = onNavigationMain)
                    patientsEntry(onNavigationMain = onNavigationMain)
                    receiptsEntry(onNavigationMain = onNavigationMain)
                }
            )
        }
    }
}