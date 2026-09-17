package yosel.dev.atti.core.navigation.top_level

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface ScreensTopLevel : NavKey {

    @Serializable
    data object Home : ScreensTopLevel

    @Serializable
    data object Directory : ScreensTopLevel

    @Serializable
    data object Consultation : ScreensTopLevel

    @Serializable
    data object Inventory : ScreensTopLevel
}