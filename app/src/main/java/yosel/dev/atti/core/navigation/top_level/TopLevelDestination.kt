package yosel.dev.atti.core.navigation.top_level

import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelDestination(
    val screen: ScreensTopLevel,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)