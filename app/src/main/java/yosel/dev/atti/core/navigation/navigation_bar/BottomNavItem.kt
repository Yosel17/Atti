package yosel.dev.atti.core.navigation.navigation_bar

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val screen: ScreensNavigationBar,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)