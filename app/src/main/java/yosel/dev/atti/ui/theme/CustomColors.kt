package yosel.dev.atti.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColors(
    //Whatsapp
    val whatsapp: Color,
    val onWhatsapp: Color,
    val whatsappContainer: Color,
    val onWhatsappContainer: Color,
    // Patient Active
    val active: Color,
    val onActive: Color,
    val activeContainer: Color,
    val onActiveContainer: Color,
    // Patient Inactive
    val inactive: Color,
    val onInactive: Color,
    val inactiveContainer: Color,
    val onInactiveContainer: Color,
    // Patient Deleted
    val deleted: Color,
    val onDeleted: Color,
    val deletedContainer: Color,
    val onDeletedContainer: Color,
    // Rangos Fisiológicos
    val rangeHypo: Color,
    val onRangeHypo: Color,
    val rangeHypoContainer: Color,
    val onRangeHypoContainer: Color,
    val rangeNormal: Color,
    val onRangeNormal: Color,
    val rangeNormalContainer: Color,
    val onRangeNormalContainer: Color,
    val rangeHyper: Color,
    val onRangeHyper: Color,
    val rangeHyperContainer: Color,
    val onRangeHyperContainer: Color,
    val rangeFever: Color,
    val onRangeFever: Color,
    val rangeFeverContainer: Color,
    val onRangeFeverContainer: Color
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        whatsapp = Color.Unspecified,
        onWhatsapp = Color.Unspecified,
        whatsappContainer = Color.Unspecified,
        onWhatsappContainer = Color.Unspecified,
        active = Color.Unspecified,
        onActive = Color.Unspecified,
        activeContainer = Color.Unspecified,
        onActiveContainer = Color.Unspecified,
        inactive = Color.Unspecified,
        onInactive = Color.Unspecified,
        inactiveContainer = Color.Unspecified,
        onInactiveContainer = Color.Unspecified,
        deleted = Color.Unspecified,
        onDeleted = Color.Unspecified,
        deletedContainer = Color.Unspecified,
        onDeletedContainer = Color.Unspecified,
        rangeHypo = Color.Unspecified,
        onRangeHypo = Color.Unspecified,
        rangeHypoContainer = Color.Unspecified,
        onRangeHypoContainer = Color.Unspecified,
        rangeNormal = Color.Unspecified,
        onRangeNormal = Color.Unspecified,
        rangeNormalContainer = Color.Unspecified,
        onRangeNormalContainer = Color.Unspecified,
        rangeHyper = Color.Unspecified,
        onRangeHyper = Color.Unspecified,
        rangeHyperContainer = Color.Unspecified,
        onRangeHyperContainer = Color.Unspecified,
        rangeFever = Color.Unspecified,
        onRangeFever = Color.Unspecified,
        rangeFeverContainer = Color.Unspecified,
        onRangeFeverContainer = Color.Unspecified
    )
}

val LightCustomColors = CustomColors(
    whatsapp = whatsappLight,
    onWhatsapp = onWhatsappLight,
    whatsappContainer = whatsappContainerLight,
    onWhatsappContainer = onWhatsappContainerLight,

    active = activeLight,
    onActive = onActiveLight,
    activeContainer = activeContainerLight,
    onActiveContainer = onActiveContainerLight,

    inactive = inactiveLight,
    onInactive = onInactiveLight,
    inactiveContainer = inactiveContainerLight,
    onInactiveContainer = onInactiveContainerLight,

    deleted = deletedLight,
    onDeleted = onDeletedLight,
    deletedContainer = deletedContainerLight,
    onDeletedContainer = onDeletedContainerLight,
    rangeHypo = rangeHypoLight,
    onRangeHypo = onRangeHypoLight,
    rangeHypoContainer = rangeHypoContainerLight,
    onRangeHypoContainer = onRangeHypoContainerLight,

    rangeNormal = rangeNormalLight,
    onRangeNormal = onRangeNormalLight,
    rangeNormalContainer = rangeNormalContainerLight,
    onRangeNormalContainer = onRangeNormalContainerLight,

    rangeHyper = rangeHyperLight,
    onRangeHyper = onRangeHyperLight,
    rangeHyperContainer = rangeHyperContainerLight,
    onRangeHyperContainer = onRangeHyperContainerLight,

    rangeFever = rangeFeverLight,
    onRangeFever = onRangeFeverLight,
    rangeFeverContainer = rangeFeverContainerLight,
    onRangeFeverContainer = onRangeFeverContainerLight
)

val DarkCustomColors = CustomColors(
    whatsapp = whatsappDark,
    onWhatsapp = onWhatsappDark,
    whatsappContainer = whatsappContainerDark,
    onWhatsappContainer = onWhatsappContainerDark,

    active = activeDark,
    onActive = onActiveDark,
    activeContainer = activeContainerDark,
    onActiveContainer = onActiveContainerDark,

    inactive = inactiveDark,
    onInactive = onInactiveDark,
    inactiveContainer = inactiveContainerDark,
    onInactiveContainer = onInactiveContainerDark,

    deleted = deletedDark,
    onDeleted = onDeletedDark,
    deletedContainer = deletedContainerDark,
    onDeletedContainer = onDeletedContainerDark,

    rangeHypo = rangeHypoDark,
    onRangeHypo = onRangeHypoDark,
    rangeHypoContainer = rangeHypoContainerDark,
    onRangeHypoContainer = onRangeHypoContainerDark,

    rangeNormal = rangeNormalDark,
    onRangeNormal = onRangeNormalDark,
    rangeNormalContainer = rangeNormalContainerDark,
    onRangeNormalContainer = onRangeNormalContainerDark,

    rangeHyper = rangeHyperDark,
    onRangeHyper = onRangeHyperDark,
    rangeHyperContainer = rangeHyperContainerDark,
    onRangeHyperContainer = onRangeHyperContainerDark,

    rangeFever = rangeFeverDark,
    onRangeFever = onRangeFeverDark,
    rangeFeverContainer = rangeFeverContainerDark,
    onRangeFeverContainer = onRangeFeverContainerDark
)