package yosel.dev.atti.core.navigation.main

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screens: NavKey {

    @Serializable
    data object Main: Screens

    @Serializable
    data object AddClient: Screens

    @Serializable
    data class DetailClient(val clientId: String, val isLocalPatients: Boolean): Screens

    @Serializable
    data class AddPatient(val patientId: String? = null, val clientId: String? = null): Screens

    @Serializable
    data class DetailPatient(val patientId: String): Screens

    @Serializable
    data object AddSupplier: Screens
}