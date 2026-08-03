package yosel.dev.atti.core.navigation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import yosel.dev.atti.screens.main.ui.MainScreen

fun EntryProviderScope<NavKey>.mainEntry(
    onNavigation: (Screens) -> Unit
){
    entry<Screens.Main> {
        MainScreen(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        )
    }
}