package yosel.dev.atti.core.navigation.main

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

@Composable
fun AppNavigation(startDestination: Screens) {

    val backStack = rememberNavBackStack(startDestination)

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = {
            backStack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            mainEntry(
                onNavigation = { screens ->
                    backStack.add(screens)
                }
            )
            addClientEntry(
                onBack = {
                    backStack.removeLastOrNull()
                }
            )
            detailClientEntry(
                onBack = {
                    backStack.removeLastOrNull()
                },
                onNavigation = { screens ->
                    backStack.add(screens)
                }
            )
            addPatientEntry(
                onBack = {
                    backStack.removeLastOrNull()
                }
            )
            detailPatientEntry(
                onBack = {
                    backStack.removeLastOrNull()
                },
                onNavigationMain = { screens ->
                    backStack.add(screens)
                }
            )
            addSupplierEntry(
                onBack = {
                    backStack.removeLastOrNull()
                }
            )
            detailSupplierEntry(
                onBack = {
                    backStack.removeLastOrNull()
                }
            )
        }
    )


}