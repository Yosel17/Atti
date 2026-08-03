package yosel.dev.atti.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.homeEntry(
    onNavigation: (Screens) -> Unit
){
    entry<Screens.Home> {
        Scaffold(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) { paddingValues ->
            Column(
                Modifier.fillMaxSize().padding(paddingValues)
            ) {
                Text("HomeScreen")
            }
        }
    }
}