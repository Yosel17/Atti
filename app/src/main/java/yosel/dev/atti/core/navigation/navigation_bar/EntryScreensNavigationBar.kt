package yosel.dev.atti.core.navigation.navigation_bar

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.launch
import yosel.dev.atti.core.utils.ObserveAsEvents
import yosel.dev.atti.screens.clients.ui.DirectoryEvent
import yosel.dev.atti.screens.clients.ui.DirectoryScreen
import yosel.dev.atti.screens.clients.ui.DirectoryViewModel
import androidx.core.net.toUri

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
        val viewModel = hiltViewModel<DirectoryViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        ObserveAsEvents(viewModel.events) { event ->
            when(event){
                is DirectoryEvent.ShowSnackBarError -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = event.message
                        )
                    }
                }
                is DirectoryEvent.NavigateToPhone -> {
                    try {
                        val intent = Intent(Intent.ACTION_DIAL, "tel:${event.phoneNumber}".toUri())
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "No se puede abrir la aplicación de teléfono"
                            )
                        }
                    }
                }
                is DirectoryEvent.NavigateToWhatsapp -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data =
                                "https://wa.me/${event.phoneNumber.filter { it.isDigit() }}".toUri()
                            setPackage("com.whatsapp")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "No se puede abrir la aplicación de WhatsApp"
                            )
                        }
                    }
                }
            }
        }

        DirectoryScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            state = state,
            snackBarHostState = snackBarHostState,
            onNavigation = { screens ->

            },
            onAction = viewModel::onAction
        )
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