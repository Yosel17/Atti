package yosel.dev.atti.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColors(
    val whatsapp: Color,
    val onWhatsapp: Color,
    val whatsappContainer: Color,
    val onWhatsappContainer: Color
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        whatsapp = Color.Unspecified,
        onWhatsapp = Color.Unspecified,
        whatsappContainer = Color.Unspecified,
        onWhatsappContainer = Color.Unspecified
    )
}

val LightCustomColors = CustomColors(
    whatsapp = whatsappLight,
    onWhatsapp = onWhatsappLight,
    whatsappContainer = whatsappContainerLight,
    onWhatsappContainer = onWhatsappContainerLight
)

val DarkCustomColors = CustomColors(
    whatsapp = whatsappDark,
    onWhatsapp = onWhatsappDark,
    whatsappContainer = whatsappContainerDark,
    onWhatsappContainer = onWhatsappContainerDark
)