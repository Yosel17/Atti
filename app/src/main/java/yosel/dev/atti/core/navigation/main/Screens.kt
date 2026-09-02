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
    data class DetailProduct(val productId: String, val showEditAction: Boolean = true): Screens

    @Serializable
    data class ServiceForm(val serviceId: String? = null): Screens

    @Serializable
    data class DetailService(val serviceId: String): Screens

    @Serializable
    data class DetailConsultation(val consultationId: String): Screens

    @Serializable
    data class AnamnesisForm(val consultationId: String, val anamnesisId: String? = null): Screens

    @Serializable
    data object Empty: Screens

    @Serializable
    data class ClinicalExamForm(val consultationId: String, val examId: String? = null) : Screens

    @Serializable
    data class PhysioConstsForm(val consultationId: String, val constsId: String? = null) : Screens

    @Serializable
    data class DiagnosisForm(val consultationId: String, val diagnosisId: String? = null) : Screens

    @Serializable
    data class TreatmentForm(val consultationId: String, val treatmentId: String? = null) : Screens
}