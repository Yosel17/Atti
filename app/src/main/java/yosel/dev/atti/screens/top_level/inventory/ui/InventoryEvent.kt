package yosel.dev.atti.screens.top_level.inventory.ui

sealed interface InventoryEvent {
    data class ShowSnackBarError(val message: String) : InventoryEvent

    data class NavigateToPhone(val phoneNumber: String): InventoryEvent

    data class NavigateToWhatsapp(val phoneNumber: String): InventoryEvent
}