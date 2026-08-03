package yosel.dev.atti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import yosel.dev.atti.core.navigation.main.AppNavigation
import yosel.dev.atti.core.navigation.main.Screens
import yosel.dev.atti.ui.theme.AttiTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AttiTheme {
                AppNavigation(startDestination = Screens.Main)
            }
        }
    }
}