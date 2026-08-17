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

    @Serializable
    data class DetailSupplier(val supplierId: String): Screens

    @Serializable
    data class ProductForm(val productId: String? = null): Screens

    @Serializable
    data class DetailProduct(val productId: String): Screens

    @Serializable
    data class ServiceForm(val serviceId: String? = null): Screens

    @Serializable
    data class DetailService(val serviceId: String): Screens
}