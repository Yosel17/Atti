package yosel.dev.atti.core.navigation.navigation_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.homeEntry(){
    entry<ScreensNavigationBar.Home> {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
        ){
            Text(text = "HomeScreen", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

fun EntryProviderScope<NavKey>.directoryEntry(){
    entry<ScreensNavigationBar.Directory> {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondary)
        ){
            Text(text = "DirectoryScreen", color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}

fun EntryProviderScope<NavKey>.consultationEntry(){
    entry<ScreensNavigationBar.Consultation> {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.tertiary)
        ){
            Text(text = "ConsultationScreen", color = MaterialTheme.colorScheme.onTertiary)
        }
    }
}

fun EntryProviderScope<NavKey>.inventoryEntry(){
    entry<ScreensNavigationBar.Inventory> {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.error)
        ){
            Text(text = "InventoryScreen", color = MaterialTheme.colorScheme.onError)
        }
    }
}