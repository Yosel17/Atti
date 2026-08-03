package yosel.dev.atti.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screens: NavKey {

    @Serializable
    data object Home: Screens
}