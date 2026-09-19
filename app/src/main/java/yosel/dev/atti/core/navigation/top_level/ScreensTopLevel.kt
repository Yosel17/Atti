package yosel.dev.atti.core.navigation.top_level

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface ScreensTopLevel : NavKey {

    @Serializable
    data object Home : ScreensTopLevel

    @Serializable
    data object Consultation : ScreensTopLevel

    @Serializable
    data object Clients : ScreensTopLevel

    @Serializable
    data object Patients : ScreensTopLevel

    @Serializable
    data object Products : ScreensTopLevel

    @Serializable
    data object Services : ScreensTopLevel

    @Serializable
    data object Suppliers : ScreensTopLevel

    @Serializable
    data object Receipts : ScreensTopLevel
}