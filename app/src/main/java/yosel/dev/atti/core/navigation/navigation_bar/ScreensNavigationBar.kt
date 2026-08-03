package yosel.dev.atti.core.navigation.navigation_bar

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface ScreensNavigationBar : NavKey {

    @Serializable
    data object Home : ScreensNavigationBar

    @Serializable
    data object Directory : ScreensNavigationBar

    @Serializable
    data object Consultation : ScreensNavigationBar

    @Serializable
    data object Inventory : ScreensNavigationBar
}